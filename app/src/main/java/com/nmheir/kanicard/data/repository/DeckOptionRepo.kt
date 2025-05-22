package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.deck.DeckOptionUsageDto
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IDeckOptionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeckOptionRepo @Inject constructor(
    private val database: KaniDatabase
) : IDeckOptionRepo {

    override fun getDeckOption(id: Long): Flow<DeckOptionEntity> {
        return database.getDeckOption(id).map {
            it ?: defaultDeckOption
        }
    }

    override fun getDeckOptionUsage(): Flow<List<DeckOptionUsageDto>> {
        return database.getDeckOptionUsages()
    }

    override suspend fun insert(option: DeckOptionEntity) {
        return database.insert(option)
    }

    override suspend fun update(deckOptionEntity: DeckOptionEntity) {
        return database.update(deckOptionEntity)
    }

    override suspend fun update(id: Long, name: String?) {
        return database.updateDeckOption(
            id = id,
            name = name
        )
    }

    override suspend fun delete(option: DeckOptionEntity) {
        return database.delete(option)
    }

    override suspend fun delete(id: Long) {
        return database.deleteDeckOption(id)
    }
}