package com.nmheir.kanicard.ui.screen.statistics.model

data class ReviewIntervalChartData(
    val barData: Map<Int, Number> = emptyMap(),
    val lineData: Map<Int, Number> = emptyMap(),
    val avgIvl: Double = 0.0
)

enum class ReviewIntervalChartState(val title: String) {
    ONE_MONTH("1 month"), HALF_PERCENT("50%"), NINETY_FIVE_PERCENT("95%"), ALL("All")
}