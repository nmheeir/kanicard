package com.nmheir.kanicard.ui.screen.statistics.model

data class FutureDueChartData(
    val barData: Map<Int, Number> = emptyMap(),
    val lineData: Map<Int, Number> = emptyMap(),
    val total: Int = 0,
    val average: Double = 0.0,
    val dueTomorrow: Int = 0,
    val dailyLoad: Int = 0
)

enum class FutureDueChartState(val title: String, val duration: Long) {
    ONE_MONTH("1 month", 30L),
    THREE_MONTHS("3 months", 90L),
    ONE_YEAR("1 year", 365L),
    ALL("All", Long.MAX_VALUE)
}