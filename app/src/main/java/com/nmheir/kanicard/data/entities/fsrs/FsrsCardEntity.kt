package com.nmheir.kanicard.data.entities.fsrs

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import java.time.OffsetDateTime

@Entity(
    tableName = "fsrs_card",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("deckId")]
)
data class FsrsCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
        fun createEmpty(fsrsCardId: Long, deckId: Long, now: OffsetDateTime): FsrsCardEntity {
            return FsrsCardEntity(
                id = fsrsCardId,
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
