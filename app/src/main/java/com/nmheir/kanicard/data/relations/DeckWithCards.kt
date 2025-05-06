package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    ) val cards: List<FsrsCardEntity>
)
