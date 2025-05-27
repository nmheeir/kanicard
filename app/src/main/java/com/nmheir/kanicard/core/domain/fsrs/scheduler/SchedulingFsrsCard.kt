package com.nmheir.kanicard.core.domain.fsrs.scheduler

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.core.domain.fsrs.model.RecordLog
import com.nmheir.kanicard.core.domain.fsrs.model.RecordLogItem
import com.nmheir.kanicard.core.domain.fsrs.model.ReviewLog
import com.nmheir.kanicard.data.enums.State
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class SchedulingFsrsCard(
    card: FsrsCard, now: OffsetDateTime
) {
    var again: FsrsCard
    var hard: FsrsCard
    var good: FsrsCard
    var easy: FsrsCard
    private var lastReview: OffsetDateTime = card.lastReview ?: card.due
    private var lastElapsedDays: Long = card.elapsedDays

    init {
        Timber.d("Now: $now")
        Timber.d("Last review: $lastReview")
        Timber.d("Card last review: ${card.lastReview}")
        card.elapsedDays =
            if (card.state == State.New) 0
            else ChronoUnit.DAYS.between(card.lastReview, now).toLong()
        Timber.d("Card elapsed days: ${card.elapsedDays}")
        card.lastReview = now
        card.reps += 1
        this.again = card.copy()
        this.hard = card.copy()
        this.good = card.copy()
        this.easy = card.copy()
    }

    fun updateState(state: State): SchedulingFsrsCard {
        when {
            state == State.New -> {
                this.again.state = State.Learning
                this.hard.state = State.Learning
                this.good.state = State.Learning
                this.easy.state = State.Review
                this.again.lapses += 1
            }

            state === State.Learning || state === State.Relearning -> {
                this.again.state = state
                this.hard.state = state
                this.good.state = State.Review
                this.easy.state = State.Review
            }

            state === State.Review -> {
                this.again.state = State.Relearning
                this.hard.state = State.Review
                this.good.state = State.Review
                this.easy.state = State.Review
                this.again.lapses += 1
            }
        }
        return this
    }

    fun schedule(
        now: OffsetDateTime,
        hardInterval: Long,
        goodInterval: Long,
        easyInterval: Long,
    ): SchedulingFsrsCard {
        Timber.d("Schedule Interval Hard: $hardInterval")
        Timber.d("Schedule Interval Good: $goodInterval")
        Timber.d("Schedule Interval Easy: $easyInterval")
        this.again.scheduledDays = 0
        this.hard.scheduledDays = hardInterval
        this.good.scheduledDays = goodInterval
        this.easy.scheduledDays = easyInterval
        this.again.due = now.plusMinutes(5)
        this.hard.due =
            if (hardInterval > 0) now.plusDays(hardInterval) else now.plusMinutes(10)
        this.good.due = now.plusDays(goodInterval)
        this.easy.due = now.plusDays(easyInterval)
        return this
    }

    fun recordLog(card: FsrsCard, now: OffsetDateTime): RecordLog =
        RecordLog(
            mapOf(
                Rating.Again to RecordLogItem(
                    card = again,
                    log = ReviewLog(
                        rating = Rating.Again,
                        state = card.state,
                        due = lastReview,
                        stability = card.stability,
                        difficulty = card.difficulty,
                        elapsedDays = card.elapsedDays,
                        lastElapsedDays = lastElapsedDays,
                        scheduledDays = card.scheduledDays,
                        review = now
                    )
                ),
                Rating.Hard to RecordLogItem(
                    card = hard,
                    log = ReviewLog(
                        rating = Rating.Hard,
                        state = card.state,
                        due = lastReview,
                        stability = card.stability,
                        difficulty = card.difficulty,
                        elapsedDays = card.elapsedDays,
                        lastElapsedDays = lastElapsedDays,
                        scheduledDays = card.scheduledDays,
                        review = now,
                    )
                ),
                Rating.Good to RecordLogItem(
                    card = good,
                    log = ReviewLog(
                        rating = Rating.Good,
                        state = card.state,
                        due = lastReview,
                        stability = card.stability,
                        difficulty = card.difficulty,
                        elapsedDays = card.elapsedDays,
                        lastElapsedDays = lastElapsedDays,
                        scheduledDays = card.scheduledDays,
                        review = now,
                    )
                ),
                Rating.Easy to RecordLogItem(
                    card = easy,
                    log = ReviewLog(
                        rating = Rating.Easy,
                        state = card.state,
                        due = lastReview,
                        stability = card.stability,
                        difficulty = card.difficulty,
                        elapsedDays = card.elapsedDays,
                        lastElapsedDays = lastElapsedDays,
                        scheduledDays = card.scheduledDays,
                        review = now
                    )
                )
            )
        )
}