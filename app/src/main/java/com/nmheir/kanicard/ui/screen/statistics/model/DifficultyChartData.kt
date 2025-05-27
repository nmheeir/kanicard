package com.nmheir.kanicard.ui.screen.statistics.model

data class DifficultyChartData(
    val barData: Map<Int, Number> = emptyMap(),
    val avgDiff: Double = 0.0
)