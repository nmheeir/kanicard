package com.nmheir.kanicard.data.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Immutable
@Entity(
    tableName = "deck_saved",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
        ),
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"]
        )
    ]
)
data class SavedDeck(
    @PrimaryKey val id: Int,
    val deckId: Int,
    val userId: Int,
    val title: String,
    val savedAt: LocalDateTime
)
