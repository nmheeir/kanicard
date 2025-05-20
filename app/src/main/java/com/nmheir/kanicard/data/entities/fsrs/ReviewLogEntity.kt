package com.nmheir.kanicard.data.entities.fsrs

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.enums.State
import java.time.OffsetDateTime

@Entity(
    tableName = "review_logs",
    foreignKeys = [ForeignKey(
        entity = FsrsCardEntity::class,
        parentColumns = ["nId"],
        childColumns = ["nId"],
        onDelete = CASCADE
    )],
    indices = [Index("nId")]
)
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nId: Long,
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
