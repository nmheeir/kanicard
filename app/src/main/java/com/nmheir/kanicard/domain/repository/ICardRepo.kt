package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import kotlinx.coroutines.flow.Flow

interface ICardRepo {
    fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?>
    fun getBrowseCard(dId: Long): Flow<List<CardBrowseData>?>
    fun getAllCards(dId: Long = -1L): Flow<List<FsrsCardEntity>>

    suspend fun cardsByDeckId(deckId: Long, pageNumber: Int): List<CardDto>

    suspend fun insert(fsrsCard: FsrsCardEntity)

    suspend fun update(fsrsCard: FsrsCardEntity)
}