package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.relations.CollectionWithDecks
import kotlinx.coroutines.flow.Flow

interface ICollectionRepo {
    suspend fun insert(collection: CollectionEntity)
    fun getAllCollections(): Flow<List<CollectionEntity>>
    fun getAllCollectionWithDecks(): Flow<List<CollectionWithDecks>>
}