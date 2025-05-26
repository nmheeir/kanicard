@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IReviewLogRepo
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartData
import com.nmheir.kanicard.ui.screen.statistics.model.CalendarChartItemData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartState
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartState
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
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.ui.screen.statistics.model.CardCountChartData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.ceil

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

    val reviewChartState = MutableStateFlow<ReviewChartState>(ReviewChartState.LAST_7_DAYS)

    val reviewChartData = combine(
        allReviewLogs,
        reviewChartState
    ) { reviewLogs, state ->
        state to reviewLogs
    }
        .filter {
            it.second.isNotEmpty()
        }
        .mapLatest { (state, reviewLogs) ->
            withContext(Dispatchers.IO) {
                calculateReviewData(state, reviewLogs)
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewChartData())

    val cardCountChartData = allCards
        .filter {
            it.isNotEmpty()
        }
        .map {
            calculateCardCountData(it)
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, CardCountChartData())

    private fun calculateFutureDueData(state: FutureDueChartState): FutureDueChartData {
        // 1. Xác định khoảng days dựa vào state
        val maxDays = when (state) {
            FutureDueChartState.ONE_MONTH -> 30L
            FutureDueChartState.THREE_MONTHS -> 90L
            FutureDueChartState.ONE_YEAR -> 365L
            FutureDueChartState.ALL -> Long.MAX_VALUE
        }

        // 2. Lấy thời điểm hiện tại (OffsetDateTime)
        val today = OffsetDateTime.now().toLocalDate()

        // 3. Tính số cards sẽ due trong tương lai, gom theo khoảng ngày
        //    barData: số card mỗi ngày
        //    lineData: số tích lũy (cumulative) đến ngày đó
        val counts = mutableMapOf<Int, Int>()
        allCards.value.forEach { card ->
            val dueDate = card.due.toLocalDate()
            val daysUntil = ChronoUnit.DAYS.between(today, dueDate).toInt()
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

        val abc = FutureDueChartData(
            barData = barData,
            lineData = lineData,
            average = average,
            dueTomorrow = dueTomorrow,
            dailyLoad = average.roundToInt()
        )

        Timber.d(abc.toString())

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

    private fun calculateReviewData(
        state: ReviewChartState,
        reviewLogs: List<ReviewLogEntity>
    ): ReviewChartData {
        val today = LocalDate.now()

        // 1. Xác định số ngày look‐back
        val totalDays = when (state) {
            ReviewChartState.LAST_7_DAYS -> 7
            ReviewChartState.LAST_30_DAYS -> 30
            ReviewChartState.LAST_90_DAYS -> 90
            ReviewChartState.LAST_YEAR -> 365
        }

        // 2. Lọc và nhóm theo ngày
        val startDate = today.minusDays((totalDays - 1).toLong())
        val byDate = reviewLogs
            .filter {
                val d = it.review.toLocalDate()
                !d.isBefore(startDate) && !d.isAfter(today)
            }
            .groupBy { it.review.toLocalDate() }

        // 3. Build barData với keys chạy từ -(totalDays-1) … 0
        val barData: Map<Int, ReviewChartCardData> = when (state) {
            ReviewChartState.LAST_YEAR -> {
                // Năm: bucket 5-ngày, reversed order
                val buckets = ceil(totalDays / 5.0).toInt()
                (0 until buckets)
                    .reversed()    // từ bucket xa nhất về gần nhất
                    .associateWith { bucketIdx ->
                        val startOff = bucketIdx * 5
                        val endOff = min(startOff + 4, totalDays - 1)
                        val bucketLogs = (startOff..endOff).flatMap { off ->
                            byDate[today.minusDays(off.toLong())].orEmpty()
                        }
                        countStates(bucketLogs)
                    }
                    // chuyển từ bucketIdx thành key = -startOff
                    .mapKeys { (bucketIdx, _) -> -bucketIdx * 5 }
            }

            else -> {
                // Ngày-lẻ: offset 0(today),1,2,… rồi reverse => (totalDays-1)..0
                (0 until totalDays)
                    .reversed()    // ví dụ 6,5,…,0
                    .associateWith { off ->
                        val date = today.minusDays(off.toLong())
                        val logs = byDate[date].orEmpty()
                        countStates(logs)
                    }
                    // rồi đổi key từ off thành -off
                    .mapKeys { (off, _) -> -off }
            }
        }

        // 4. Tính lineData
        val sortedKeys = barData.keys.sorted()
        val running = mutableListOf<Number>()
        var sum = 0
        for (k in sortedKeys) {
            val c = barData[k]!!
            val dayTotal = c.learning + c.relearning + c.young + c.mature
            sum += dayTotal
            running += sum
        }
        val lineData = sortedKeys.zip(running).toMap()

        // 5. Thống kê
        val dailyTotals = sortedKeys.map { k ->
            val c = barData[k]!!
            c.learning + c.relearning + c.young + c.mature
        }
        val totalReviews = dailyTotals.sum()
        val daysStudied = dailyTotals.count { it > 0 }
        val periodDays = sortedKeys.size
        val avgStudiedDay = if (daysStudied > 0) totalReviews.toDouble() / daysStudied else 0.0
        val avgOverPeriod = if (periodDays > 0) totalReviews.toDouble() / periodDays else 0.0

        return ReviewChartData(
            barData = barData,
            lineData = lineData,
            dayStudied = daysStudied,
            total = totalReviews,
            averageDayStudied = avgStudiedDay,
            averageOverPeriod = avgOverPeriod
        )
    }

    private fun calculateCardCountData(
        fsrsCards: List<FsrsCardEntity>
    ): CardCountChartData {
        val total = fsrsCards.size

        val newCount = fsrsCards.count { it.state == State.New }
        val learningCount = fsrsCards.count { it.state == State.Learning }
        val relearningCount = fsrsCards.count { it.state == State.Relearning }

        // Chỉ những card đã Review mới tính Young/Mature
        val reviewCards = fsrsCards.filter { it.state == State.Review }
        val youngCount = reviewCards.count { it.scheduledDays < 21 }
        val matureCount = reviewCards.size - youngCount

        return CardCountChartData(
            total     = total,
            new       = newCount,
            learning  = learningCount,
            relearning= relearningCount,
            young     = youngCount,
            mature    = matureCount
        )
    }


    // Helper để đếm 4 state theo rule Anki
    private fun countStates(logs: List<ReviewLogEntity>): ReviewChartCardData {
        var l = 0;
        var r = 0;
        var y = 0;
        var m = 0
        for (rlog in logs) {
            when (rlog.state) {
                State.Learning -> l++
                State.Relearning -> r++
                State.Review ->
                    if (rlog.scheduledDays < 21) y++ else m++

                else -> {} // New bỏ qua
            }
        }
        return ReviewChartCardData(l, r, y, m)
    }


    fun onAction(action: StatisticUiAction) {
        when (action) {
            is StatisticUiAction.ChangeFutureDueChartState -> {
                futureDueChartState.value = action.state
            }

            is StatisticUiAction.ChangeCalendarChartState -> {
                calendarChartState.value = action.year
            }

            is StatisticUiAction.ChangeReviewCHartState -> {
                reviewChartState.value = action.state
            }
        }
    }
}

sealed interface StatisticUiAction {
    data class ChangeFutureDueChartState(val state: FutureDueChartState) : StatisticUiAction
    data class ChangeCalendarChartState(val year: Int) : StatisticUiAction
    data class ChangeReviewCHartState(val state: ReviewChartState) : StatisticUiAction
}