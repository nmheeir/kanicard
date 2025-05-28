package com.nmheir.kanicard.data.entities.fsrs

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.enums.State
import java.time.LocalDateTime

@Entity(
    tableName = "fsrs_card",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["dId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["nId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("dId"),
        Index("nId")
    ]
)
data class FsrsCardEntity(
    @PrimaryKey val nId: Long,
    val dId: Long,
    val due: LocalDateTime,
    val stability: Double,
    val difficulty: Double,
    val elapsedDays: Long,
    val scheduledDays: Long,
    val reps: Long,
    val lapses: Long,
    val state: State,
    val lastReview: LocalDateTime?
) {
    companion object {
        fun createNew(dId: Long, nId: Long): FsrsCardEntity {
            return FsrsCardEntity(
                dId = dId,
                nId = nId,
                due = LocalDateTime.now(),
                stability = 0.0,
                difficulty = 0.0,
                elapsedDays = 0,
                scheduledDays = 0,
                reps = 0,
                lapses = 0,
                state = State.New,
                lastReview = null
            )
        }
    }

    fun toFsrsCard(): FsrsCard {
        return FsrsCard(
            due = this.due,
            stability = this.stability,
            difficulty = this.difficulty,
            elapsedDays = this.elapsedDays,
            scheduledDays = this.scheduledDays,
            reps = this.reps,
            lapses = this.lapses,
            state = this.state,
            lastReview = this.lastReview
        )
    }
}
