package com.nmheir.kanicard.ui.screen.statistics.model

data class ReviewChartData(
    val barData: Map<Int, ReviewChartCardData> = emptyMap(),
    val lineData: Map<Int, Number> = emptyMap(),
    val dayStudied: Int = 0,
    val total: Int = 0,
    val averageDayStudied: Double = 0.0,
    val averageOverPeriod: Double = 0.0
)

data class ReviewChartCardData(
    val learning: Int,
    val relearning: Int,
    val young: Int,
    val mature: Int
)

enum class ReviewChartState(val title: String) {
    LAST_7_DAYS("7 days"),
    LAST_30_DAYS("30 days"),
    LAST_90_DAYS("90 days"),
    LAST_YEAR("1 year")
}