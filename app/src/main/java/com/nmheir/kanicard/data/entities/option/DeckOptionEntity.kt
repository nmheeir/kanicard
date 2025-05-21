package com.nmheir.kanicard.data.entities.option

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import java.time.OffsetDateTime

@Entity(
    tableName = "deck_options"
)
data class DeckOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
    val newPerDay: Long,
    val revPerDay: Long,
    val fsrsParams: List<Double>,
    val autoAudio: Boolean,
    val autoAnswer: Boolean
)

val defaultDeckOption = DeckOptionEntity(
    id = 1L,
    name = "Default",
    createdAt = OffsetDateTime.now(),
    updatedAt = null,
    newPerDay = 20,
    revPerDay = 200,
    fsrsParams = listOf(
        0.4, 0.6, 2.4, 5.8, 4.93, 0.94, 0.86, 0.01, 1.49, 0.14, 0.94, 2.18, 0.05,
        0.34, 1.26, 0.29, 2.61,
    ),
    autoAudio = false,
    autoAnswer = false
)