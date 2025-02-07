package com.nmheir.kanicard.data.entities.fsrs

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nmheir.kanicard.core.domain.fsrs.model.State
import com.nmheir.kanicard.data.entities.DownloadedCardEntity
import java.time.OffsetDateTime

@Entity(
    tableName = "fsrs_card",
    foreignKeys = [
        ForeignKey(
            entity = DownloadedCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FsrsCardEntity(
    @PrimaryKey val cardId: Long,
    val deckId: Long,
    val due: OffsetDateTime,
    val stability: Double,
    val difficulty: Double,
    val elapsedDays: Long,
    val scheduledDays: Long,
    val reps: Long,
    val lapses: Long,
    val state: State,
    val lastReview: OffsetDateTime?
) {
    companion object {
        fun createEmpty(cardId: Long, deckId: Long, now: OffsetDateTime): FsrsCardEntity {
            return FsrsCardEntity(
                cardId = cardId,
                deckId = deckId,
                due = now,
                stability = 0.0,
                difficulty = 0.0,
                elapsedDays = 0,
                scheduledDays = 0,
                reps = 0,
                lapses = 0,
                state = State.New,
                lastReview = null,
            )
        }
    }
}
