package com.nmheir.kanicard.data.remote.repository.irepo

import com.nmheir.kanicard.data.entities.DeckEntity

interface IDeckRepo {
    suspend fun create(deck: DeckEntity)
    suspend fun delete(deck: DeckEntity)
    suspend fun update(deck: DeckEntity)

    suspend fun fetchMyDecks(): List<DeckEntity>
}