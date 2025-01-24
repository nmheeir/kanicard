package com.nmheir.kanicard.data.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Immutable
@Entity(
    tableName = "decks"
)
data class DeckEntity(
    @PrimaryKey val id: Long,
    val userId: Int,
    val title: String,
    val thumbnail: String,
    val description: String,
    val isPublic: Boolean,
    val createdAt: LocalDateTime
)
