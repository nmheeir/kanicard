package com.nmheir.kanicard.data.dto.deck

data class DeckWidgetData(
    val deckId: Long,
    val name: String,
    val reviewCount: Int,
    val learnCount: Int,
    val newCount: Int
)