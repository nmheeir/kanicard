package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.core.domain.fsrs.model.ReviewLog
import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import kotlinx.coroutines.flow.Flow

interface IReviewRepo {
    fun getReviewsByNoteId(nId: Long): Flow<List<ReviewLogEntity>>

    suspend fun insert(nId: Long, reviewLog: ReviewLog)
}