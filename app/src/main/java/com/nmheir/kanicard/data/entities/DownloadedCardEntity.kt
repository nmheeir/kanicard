package com.nmheir.kanicard.data.entities

import androidx.room.Entity

@Entity(tableName = "downloaded_cards")
data class DownloadedCardEntity(
    val id: Long,
    val deckId: Long,
    val question: String? = null,
    val answer: String? = null,
    val hint: String? = null,
    val createAt: String? = null,
    val lastUpdated: String? = null
)
