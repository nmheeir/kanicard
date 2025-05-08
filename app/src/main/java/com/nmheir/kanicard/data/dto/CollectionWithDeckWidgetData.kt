package com.nmheir.kanicard.data.dto

import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.CollectionEntity

data class CollectionWithDeckWidgetData(
    val collection: CollectionEntity,
    val deckWidgetDatas: List<DeckWidgetData>
)
