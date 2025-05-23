package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import kotlinx.coroutines.flow.Flow

interface IReviewLogRepo {
    fun allReviewLogs(): Flow<List<ReviewLogEntity>>
}