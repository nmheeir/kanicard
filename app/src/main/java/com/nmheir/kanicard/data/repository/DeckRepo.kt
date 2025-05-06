package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IDeckRepo
import kotlinx.coroutines.flow.Flow

class DeckRepo(
    private val database: KaniDatabase
) : IDeckRepo {

    override fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>> {
        return database.getAllDeckWidgetData()
    }

    override fun insert(deck: DeckDto) {
        database.insert(deck.toDeckEntity())
    }

    override fun delete(deck: DeckDto) {
        database.delete(deck.toDeckEntity())
    }

    override fun update(deck: DeckDto) {
        database.update(deck.toDeckEntity())
    }

}