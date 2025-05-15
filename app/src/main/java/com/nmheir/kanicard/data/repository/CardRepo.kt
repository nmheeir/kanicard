package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.ICardRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.mapNotNull

class CardRepo(
    private val database: KaniDatabase
) : ICardRepo {
    override suspend fun cardsByDeckId(
        deckId: Long,
        pageNumber: Int
    ): List<CardDto> {
        return emptyList()
    }

    override suspend fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?> {
        return database.getDueCardsToday(deckId)
    }

    override suspend fun insert(fsrsCard: FsrsCardEntity) {
        database.insert(fsrsCard)
    }

}