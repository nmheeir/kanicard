package com.nmheir.kanicard.ui.screen.statistics.model

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