package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.core.domain.fsrs.model.ReviewLog
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IReviewRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewRepo @Inject constructor(
    private val database: KaniDatabase
) : IReviewRepo {
    override fun getReviewsByNoteId(nId: Long): Flow<List<ReviewLogEntity>> {
        return database.getReviewsByNoteId(nId)
    }

    override suspend fun insert(nId: Long, reviewLog: ReviewLog) {
        val reviewLogEntity = ReviewLogEntity(
            nId = nId,
            rating = reviewLog.rating,
            state = reviewLog.state,
            due = reviewLog.due,
            stability = reviewLog.stability,
            difficulty = reviewLog.difficulty,
            elapsedDays = reviewLog.elapsedDays,
            lastElapsedDays = reviewLog.lastElapsedDays,
            scheduledDays = reviewLog.scheduledDays,
            review = reviewLog.review
        )
        database.insert(reviewLogEntity)
    }
}