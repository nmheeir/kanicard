package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.fsrs.ReviewLogEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IReviewLogRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewLogRepo @Inject constructor(
    private val database: KaniDatabase
) : IReviewLogRepo {
    override fun allReviewLogs(): Flow<List<ReviewLogEntity>> {
        return database.getReviewLogs()
    }
}