package com.nmheir.kanicard.data.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(
    tableName = "deck_previews"
)
data class DeckPreview(
    @PrimaryKey val id: Long,
    val deckId: Int,
    val userId: Int,
    val newWord: Int,
    val learnWord: Int,
    val reviewWork: Int
)
