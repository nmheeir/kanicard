package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import kotlinx.coroutines.flow.Flow

interface ICardRepo {
    suspend fun cardsByDeckId(deckId: Long, pageNumber: Int): List<CardDto>
    suspend fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?>
}