package com.nmheir.kanicard.data.dto.deck

data class DeckConfigDto(
    val id: Long,
    val newCardsPerDay: Int,
    val maxReviewsPerDay: Int,
    val initialEase: Float,
    val graduatingInterval: Int,
    val easyIvl: Int,
    val lapseIvl: Int,
    val stepsNew: List<Int>,
    val stepsLearning: List<Int>,
    val stepRelearning: List<Int>,
    val learningEase: Float,
    val easyBonus: Float,
    val ivlModifier: Float,
    val timeLimitSec: Int,
)
