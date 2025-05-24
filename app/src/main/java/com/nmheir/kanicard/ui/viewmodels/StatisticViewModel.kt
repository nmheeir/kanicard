package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IReviewLogRepo
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartData
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartItemData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val reviewLogRepo: IReviewLogRepo,
    private val cardRepo: ICardRepo
) : ViewModel() {

    private val dId = savedStateHandle.get<Long>("dId") ?: -1L

    private val allCards = cardRepo.getAllCards(dId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val allReviewLogs = reviewLogRepo.allReviewLogs()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val futureDueChartDataMap =
        MutableStateFlow<Map<FutureDueChartState, FutureDueChartData>>(emptyMap())

    val futureDueChartState = MutableStateFlow<FutureDueChartState>(FutureDueChartState.ONE_MONTH)

    //Can be extend in future
    val calendarChartState = MutableStateFlow<Int>(LocalDateTime.now().year)

    val calendarChartDate = combine(
        calendarChartState,
        allReviewLogs
    ) { year, reviewLogs ->
        reviewLogs to year
    }
        .filter { (reviewLogs, _) ->
            reviewLogs.isNotEmpty()
        }
        .map { (reviewLogs, year) ->
            calculateChartData(year, reviewLogs)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarChartData())

    // 1. Kết hợp 2 flow: allCards + futureDueChartState
    val futureDueChartData = combine(
        allCards,
        futureDueChartState
    ) { cards, state ->
        cards to state
    }
        // 2. (Tuỳ chọn) Bỏ qua emission đầu cho đến khi `cards` không còn empty
        .filter { (cards, _) -> cards.isNotEmpty() }
        // 3. Khi có cards mới hoặc state thay đổi, tính toán
        .map { (cards, state) ->
            calculateFutureDueData(state)
        }
        // 4. Loại trùng lặp nếu cần
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            // Bạn cần một default cho stateIn, có thể là empty data
            FutureDueChartData()
        )

    private fun calculateFutureDueData(state: FutureDueChartState): FutureDueChartData {
        // 1. Xác định khoảng days dựa vào state
        val maxDays = when (state) {
            FutureDueChartState.ONE_MONTH -> 30L
            FutureDueChartState.THREE_MONTHS -> 90L
            FutureDueChartState.ONE_YEAR -> 365L
            FutureDueChartState.ALL -> Long.MAX_VALUE
        }

        // 2. Lấy thời điểm hiện tại (OffsetDateTime)
        val today = OffsetDateTime.now()

        // 3. Tính số cards sẽ due trong tương lai, gom theo khoảng ngày
        //    barData: số card mỗi ngày
        //    lineData: số tích lũy (cumulative) đến ngày đó
        val counts = mutableMapOf<Int, Int>()
        allCards.value.forEach { card ->
            val daysUntil = ChronoUnit.DAYS.between(today, card.due).toInt()
            if (daysUntil in 0..maxDays) {
                counts[daysUntil] = (counts[daysUntil] ?: 0) + 1
            }
        }

        // 4. Chuẩn bị barData và lineData
        val barData = (0..min(maxDays.toInt(), counts.keys.maxOrNull() ?: 0)).associateWith {
            counts[it] ?: 0
        }
        val lineData = barData.entries
            .sortedBy { it.key }
            .runningFold(0 to emptyMap<Int, Number>()) { acc, entry ->
                val (prevSum, _) = acc
                val newSum = prevSum + entry.value
                newSum to (acc.second + (entry.key to newSum))
            }
            .last().second

        // 5. Tính thêm các chỉ số:
        //    - dueTomorrow: số card due ngày mai
        //    - dailyLoad: trung bình số card due mỗi ngày trong khoảng
        val dueTomorrow = barData[1] ?: 0
        val total = barData.values.sum()
        val daysCounted = barData.size.takeIf { it > 0 } ?: 1
        val average = total.toDouble() / daysCounted

        return FutureDueChartData(
            barData = barData,
            lineData = lineData,
            average = average,
            dueTomorrow = dueTomorrow,
            dailyLoad = average.roundToInt()
        )
    }

    private fun calculateChartData(
        year: Int,
        reviewLogs: List<ReviewLogEntity>
    ): CalendarChartData {
        val today = LocalDate.now()
        val currentYear = today.year
        val currentMonth = today.monthValue

        // Chỉ lấy tới tháng hiện tại nếu là năm nay, ngược lại lấy 1..12
        val months = if (year == currentYear) 1..currentMonth else 1..12

        // Gom log theo ngày
        val countByDate = reviewLogs
            .filter { it.review.toLocalDate().year == year }
            .groupBy { it.review.toLocalDate() }
            .mapValues { it.value.size }

        val monthToItems = months.associateWith { month ->
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            (1..daysInMonth).map { day ->
                val date = LocalDate.of(year, month, day)
                CalendarChartItemData(
                    day = date,
                    reviewCount = countByDate[date] ?: 0
                )
            }
        }

        return CalendarChartData(data = monthToItems)
    }


    fun onAction(action: StatisticUiAction) {
        when (action) {
            is StatisticUiAction.ChangeFutureDueChartState -> {
                futureDueChartState.value = action.state
            }

            is StatisticUiAction.ChangeCalendarChartState -> {
                calendarChartState.value = action.year
            }
        }
    }
}

sealed interface StatisticUiAction {
    data class ChangeFutureDueChartState(val state: FutureDueChartState) : StatisticUiAction
    data class ChangeCalendarChartState(val year: Int) : StatisticUiAction
}