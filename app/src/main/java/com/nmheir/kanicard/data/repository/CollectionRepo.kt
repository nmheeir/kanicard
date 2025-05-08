package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.relations.CollectionWithDecks
import com.nmheir.kanicard.domain.repository.ICollectionRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CollectionRepo @Inject constructor(
    private val database: KaniDatabase
) : ICollectionRepo {
    override suspend fun insert(collection: CollectionEntity) {
        database.insert(collection)
    }

    override fun getAllCollections(): Flow<List<CollectionEntity>> {
        return database.getCollections()
    }

    override fun getAllCollectionWithDecks(): Flow<List<CollectionWithDecks>> {
        val deckWidgetData = database.getAllDeckWidgetData()

        return database.getAllCollectionsWithDecks()
    }
}