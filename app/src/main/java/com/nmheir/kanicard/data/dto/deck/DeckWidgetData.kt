package com.nmheir.kanicard.data.dto.deck

import java.time.LocalDateTime

data class DeckWidgetData(
    val deckId: Long,
    val optionId: Long,
    val name: String,
    val reviewCount: Int,
    val learnCount: Int,
    val newCount: Int,
    val dueToday: Int,
    val lastReview: LocalDateTime? = null
)