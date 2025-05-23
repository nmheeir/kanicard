package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.deck.DeckData
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import kotlinx.coroutines.flow.Flow

interface IDeckRepo {
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>
    fun getAllDecks(): Flow<List<DeckDto>?>
    fun getAllCollections(): Flow<List<CollectionEntity>>
    fun getDeckDataById(dId: Long): Flow<DeckData>
    fun queryDeck(id: Long? = null, name: String? = null): Flow<DeckDto?>

    suspend fun insert(deck: DeckDto)
    suspend fun insert(collection: CollectionEntity)

    fun delete(deck: DeckDto)
    suspend fun deleteById(id: Long)

    suspend fun update(deck: DeckDto)
    suspend fun update(
        id: Long,
        name: String? = null,
        description: String? = null,
        optionId: Long = 1L
    )
    suspend fun updateName(id: Long, name: String)
}