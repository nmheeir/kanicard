package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.deck.DeckOptionUsageDto
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import kotlinx.coroutines.flow.Flow

interface IDeckOptionRepo {

    fun getDeckOption(id: Long): Flow<DeckOptionEntity>
    fun getDeckOptionUsage(): Flow<List<DeckOptionUsageDto>>

    suspend fun insert(option: DeckOptionEntity)

    suspend fun update(deckOptionEntity: DeckOptionEntity)
    suspend fun update(id: Long, name: String? = null)

    suspend fun delete(option: DeckOptionEntity)
    suspend fun delete(id: Long)
}