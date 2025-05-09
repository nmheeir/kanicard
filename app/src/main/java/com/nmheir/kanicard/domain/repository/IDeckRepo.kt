package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import kotlinx.coroutines.flow.Flow

interface IDeckRepo {
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>
    fun getAllDecks(): Flow<List<DeckDto>?>
    suspend fun insert(deck: DeckDto)
    suspend fun insert(collection: CollectionEntity)
    fun delete(deck: DeckDto)
    suspend fun deleteById(id: Long)
    suspend fun update(deck: DeckDto)
    suspend fun updateName(id: Long, name: String)
    fun getAllCollections(): Flow<List<CollectionEntity>>
    suspend fun queryDeck(id: Long? = null, name: String? = null): DeckDto?
}