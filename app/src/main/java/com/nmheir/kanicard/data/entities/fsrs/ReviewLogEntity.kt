package com.nmheir.kanicard.data.entities.fsrs

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.nmheir.kanicard.core.domain.fsrs.model.Rating
import com.nmheir.kanicard.core.domain.fsrs.model.State
import java.time.OffsetDateTime

@Entity(
    tableName = "review_log",
    foreignKeys = [ForeignKey(
        entity = FsrsCardEntity::class,
        parentColumns = ["cardId"],
        childColumns = ["fsrsCardId"],
        onDelete = CASCADE
    )]
)
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fsrsCardId: Long,
    val rating: Rating,
    val state: State,
    val due: OffsetDateTime,
    val stability: Double,
    val difficulty: Double,
    val elapsedDays: Long,
    val lastElapsedDays: Long,
    val scheduledDays: Long,
    val review: OffsetDateTime
)
