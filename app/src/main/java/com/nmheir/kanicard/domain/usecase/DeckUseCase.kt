package com.nmheir.kanicard.domain.usecase

import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo

data class DeckUseCase(
    val create: CreateDeck,
    val fetchMyDeck: FetchMyDeck,
    val getDeckDetail: GetDeckDetail
)

class GetDeckDetail(
    private val iDeckRepo: IDeckRepo
) {
    suspend operator fun invoke(deckId: Long): DeckDetailDto {
        return iDeckRepo.getDeckDetail(deckId)
    }
}

class CreateDeck(
    private val iDeckRepo: IDeckRepo
) {
    suspend operator fun invoke(deck: DeckEntity) {
        iDeckRepo.create(deck)
    }
}

class FetchMyDeck(
    private val iDeckRepo: IDeckRepo
) {
    suspend operator fun invoke(): List<DeckEntity> {
        return iDeckRepo.fetchMyDecks()
    }
}