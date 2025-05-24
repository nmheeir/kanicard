package com.nmheir.kanicard.ui.screen.statistics.model

import java.time.LocalDate

data class CalendarChartData(
    val data: Map<Int, List<CalendarChartItemData>> = emptyMap()  // 12 months in a year
)

data class CalendarChartItemData(
    val day: LocalDate,
    val reviewCount: Int
)