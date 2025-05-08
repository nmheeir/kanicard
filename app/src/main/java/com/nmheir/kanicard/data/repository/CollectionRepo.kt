package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.ICollectionRepo
import javax.inject.Inject

class CollectionRepo @Inject constructor(
    private val database: KaniDatabase
) : ICollectionRepo {
    override suspend fun insert(collection: CollectionEntity) {
        database.insert(collection)
    }
}