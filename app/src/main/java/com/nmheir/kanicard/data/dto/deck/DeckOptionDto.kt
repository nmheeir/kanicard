package com.nmheir.kanicard.data.dto.deck

data class DeckOptionDto(
    val optionGroupId: Long,
    val newCardsPerDay: Int,
    val maxReviewsPerDay: Int,
    val initialEase: Float,
    val graduatingInterval: Int,
    val easyInterval: Int,
    val lapseInterval: Int,
    val stepsNew: List<Int>,       // JSON ↔ List<Int>
    val stepsLearning: List<Int>,
    val relearnSteps: List<Int>,
    val learningEase: Float,
    val easyBonus: Float,
    val intervalModifier: Float,
    val timeLimitSec: Int,
)
