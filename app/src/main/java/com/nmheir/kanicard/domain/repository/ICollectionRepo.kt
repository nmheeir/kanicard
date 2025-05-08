package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.deck.CollectionEntity

interface ICollectionRepo {
    suspend fun insert(collection: CollectionEntity)
}