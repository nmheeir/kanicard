package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IDeckRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class DeckRepo(
    private val database: KaniDatabase
) : IDeckRepo {

    override fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>> {
        return database.getAllDeckWidgetData()
    }

    override fun getAllDecks(): Flow<List<DeckDto>?> {
        return database.allDecks().mapNotNull {
            it?.map { deckEntity ->
                deckEntity.toDeckDto()
            }
        }
    }

    override suspend fun insert(deck: DeckDto) {
        database.insert(deck.toDeckEntity())
    }

    override suspend fun insert(collection: CollectionEntity) {
        database.insert(collection)
    }

    override fun delete(deck: DeckDto) {
        database.delete(deck.toDeckEntity())
    }

    override suspend fun deleteById(id: Long) {
        database.deleteDeck(id)
    }

    override suspend fun update(deck: DeckDto) {
        database.update(deck.toDeckEntity())
    }

    override suspend fun updateName(id: Long, name: String) {
        database.updateDeckName(id, name)
    }

    override fun getAllCollections(): Flow<List<CollectionEntity>> {
        return database.getCollections()
    }

    override suspend fun queryDeck(
        id: Long?,
        name: String?
    ): DeckDto? {
        return database.deck(id, name)?.toDeckDto()
    }

}