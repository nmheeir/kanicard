package com.nmheir.kanicard.core.domain.fsrs.model

import com.nmheir.kanicard.data.enums.State
import java.time.LocalDateTime

data class FsrsCard(
    var due: LocalDateTime,
    var stability: Double,
    var difficulty: Double,
    var elapsedDays: Long,
    var scheduledDays: Long,
    var reps: Long,
    var lapses: Long,
    var state: State,
    var lastReview: LocalDateTime?
) {
    companion object {
        fun createEmpty(now: LocalDateTime): FsrsCard =
            FsrsCard(
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
