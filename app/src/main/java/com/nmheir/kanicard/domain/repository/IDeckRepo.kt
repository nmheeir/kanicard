package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import kotlinx.coroutines.flow.Flow

interface IDeckRepo {
    fun getAllDeckWidgetData(): Flow<List<DeckWidgetData>>
    fun insert(deck: DeckDto)
    fun delete(deck: DeckDto)
    fun update(deck: DeckDto)
}