package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.data.entities.deck.DeckEntity

data class CollectionWithDecks(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "colId"
    )
    val decks: List<DeckEntity>
)
