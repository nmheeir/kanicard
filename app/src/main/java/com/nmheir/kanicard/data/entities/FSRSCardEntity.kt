package com.nmheir.kanicard.data.entities

import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

data class FSRSCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val due: LocalDateTime,
    val stability: Double,
    val difficulty: Double,
    val elapsedDays: Double,
    val scheduledDays: Double,
    val reps: Int,
    val lapses: Int,
    val status: String,
    val lastReview: LocalDateTime
)
