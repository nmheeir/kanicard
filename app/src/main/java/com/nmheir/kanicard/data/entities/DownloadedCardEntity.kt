package com.nmheir.kanicard.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_cards")
data class DownloadedCardEntity(
    @PrimaryKey val id: Long,
    val deckId: Long,
    val question: String? = null,
    val answer: String? = null,
    val hint: String? = null,
    val createAt: String? = null,
    val lastUpdated: String? = null
)
