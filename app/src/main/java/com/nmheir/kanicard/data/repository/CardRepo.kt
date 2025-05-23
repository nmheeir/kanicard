package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.card.CardBrowseData
import com.nmheir.kanicard.data.dto.card.toCardBrowseData
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.ICardRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class CardRepo @Inject constructor(
    private val database: KaniDatabase
) : ICardRepo {
    override suspend fun cardsByDeckId(
        deckId: Long,
        pageNumber: Int
    ): List<CardDto> {
        return emptyList()
    }

    override fun getDueCardsToday(deckId: Long): Flow<List<FsrsCardEntity>?> {
        return database.getDueCardsToday(deckId)
    }

    override fun getBrowseCard(dId: Long): Flow<List<CardBrowseData>?> {
        return database.getBrowseCards(dId).map {
            it.map { it.toCardBrowseData() }
        }
    }

    override fun getAllCards(dId: Long): Flow<List<FsrsCardEntity>> {
        return if (dId == -1L) {
            database.getAllCards()
                .onEach { list ->
                    Timber.d("Statistics deckId=-1 emitted ${list.size} cards: $list")
                }
        } else {
            database.getAllCards(dId)
                .onEach { list ->
                    Timber.d("Statistics deckId=$dId emitted ${list.size} cards: $list")
                }
        }
    }

    override suspend fun insert(fsrsCard: FsrsCardEntity) {
        database.insert(fsrsCard)
    }

    override suspend fun update(fsrsCard: FsrsCardEntity) {
        database.update(fsrsCard)
    }

}