package com.nmheir.kanicard.data.dto.deck

import java.time.OffsetDateTime

data class DeckWidgetData(
    val deckId: Long,
    val name: String,
    val reviewCount: Int,
    val learnCount: Int,
    val newCount: Int,
    val dueToday: Int,
    val lastReview: OffsetDateTime? = null
)