package com.nmheir.kanicard.ui.screen.statistics.model

data class ReviewChartModelData(
    val barData: Map<Int, Number> = emptyMap(),
    val lineData: Map<Int, Number> = emptyMap(),
)
