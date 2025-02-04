package com.nmheir.kanicard.data.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Immutable
@Entity(
    tableName = "deck_saved"
)
data class SavedDeck(
    @PrimaryKey val id: Int,
    val deckId: Int,
    val userId: Int,
    val title: String,
    val savedAt: LocalDateTime
)
