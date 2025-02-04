package com.nmheir.kanicard.data.remote.repository.irepo

import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto

interface IDeckRepo {
    suspend fun create(deck: DeckDto)
    suspend fun delete(deck: DeckDto)
    suspend fun update(deck: DeckDto)

    suspend fun getDeckDetail(deckId: Long): DeckDetailDto

    suspend fun fetchMyDecks(): List<DeckDto>
}