package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IReviewLogRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.time.OffsetDateTime
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

    private val futureDueChartDataMap =
        MutableStateFlow<Map<FutureDueChartState, FutureDueChartData>>(emptyMap())

    val futureDueChartState = MutableStateFlow<FutureDueChartState>(FutureDueChartState.ONE_MONTH)

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

    fun onAction(action: StatisticUiAction) {
        when (action) {
            is StatisticUiAction.ChangeFutureDueChartState -> {
                futureDueChartState.value = action.state
            }
        }
    }
}

sealed interface StatisticUiAction {
    data class ChangeFutureDueChartState(val state: FutureDueChartState) : StatisticUiAction
}

data class FutureDueChartData(
    val barData: Map<Int, Number> = emptyMap(),
    val lineData: Map<Int, Number> = emptyMap(),
    val average: Double = 0.0,
    val dueTomorrow: Int = 0,
    val dailyLoad: Int = 0
)

enum class FutureDueChartState(val title: String) {
    ONE_MONTH("1 month"), THREE_MONTHS("3 months"), ONE_YEAR("1 year"), ALL("All")
}


val fakeFutureDueData: Map<FutureDueChartState, FutureDueChartData> = mapOf(

    // 1 tháng: giả định lấy mẫu 7 ngày đầu
    FutureDueChartState.ONE_MONTH to FutureDueChartData(
        barData = mapOf(
            0 to 2,   // hôm nay có 2 thẻ đến hạn
            1 to 5,   // ngày mai 5 thẻ
            2 to 3,
            3 to 4,
            4 to 1,
            5 to 0,
            6 to 2
        ),
        lineData = mapOf(
            0 to 2,   // cumulative
            1 to 7,
            2 to 10,
            3 to 14,
            4 to 15,
            5 to 15,
            6 to 17
        ),
        average = 17.0 / 7,   // ≈2.43
        dueTomorrow = 5,          // barData[1]
        dailyLoad = (17.0 / 7).roundToInt() // =2
    ),

    // 3 tháng: thử với mẫu 7 ngày đầu, volume lớn hơn
    FutureDueChartState.THREE_MONTHS to FutureDueChartData(
        barData = mapOf(0 to 8, 1 to 12, 2 to 9, 3 to 7, 4 to 5, 5 to 4, 6 to 3),
        lineData = mapOf(0 to 8, 1 to 20, 2 to 29, 3 to 36, 4 to 41, 5 to 45, 6 to 48),
        average = 48.0 / 7,   // ≈6.86
        dueTomorrow = 12,
        dailyLoad = (48.0 / 7).roundToInt() // =7
    ),

    // 1 năm: sample 7 ngày đầu
    FutureDueChartState.ONE_YEAR to FutureDueChartData(
        barData = mapOf(0 to 15, 1 to 18, 2 to 20, 3 to 22, 4 to 17, 5 to 14, 6 to 10),
        lineData = mapOf(0 to 15, 1 to 33, 2 to 53, 3 to 75, 4 to 92, 5 to 106, 6 to 116),
        average = 116.0 / 7,  // ≈16.57
        dueTomorrow = 18,
        dailyLoad = (116.0 / 7).roundToInt() // =17
    ),

    // ALL: lấy sample 7 ngày, có thể “tất cả” tức không giới hạn ngày
    FutureDueChartState.ALL to FutureDueChartData(
        barData = mapOf(0 to 30, 1 to 25, 2 to 28, 3 to 22, 4 to 20, 5 to 18, 6 to 15),
        lineData = mapOf(0 to 30, 1 to 55, 2 to 83, 3 to 105, 4 to 125, 5 to 143, 6 to 158),
        average = 158.0 / 7,  // ≈22.57
        dueTomorrow = 25,
        dailyLoad = (158.0 / 7).roundToInt() // =23
    )
)
