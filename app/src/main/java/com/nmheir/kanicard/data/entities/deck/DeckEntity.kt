package com.nmheir.kanicard.data.entities.deck

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "decks"
)
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime,
    val flags: Int
)