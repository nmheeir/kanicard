package com.nmheir.kanicard.ui.screen.statistics.model

data class CardCountChartData(
    val total: Int = 0,
    val new: Int = 0,
    val learning: Int = 0,
    val relearning: Int = 0,
    val young: Int = 0,
    val mature: Int = 0
)