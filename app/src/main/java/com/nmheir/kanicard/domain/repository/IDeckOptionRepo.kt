package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.deck.DeckOptionUsageDto
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import kotlinx.coroutines.flow.Flow

interface IDeckOptionRepo {

    fun getDeckOption(id: Long): Flow<DeckOptionEntity>
    fun getDeckOptionUsage(): Flow<List<DeckOptionUsageDto>>

    suspend fun update(deckOptionEntity: DeckOptionEntity)
}