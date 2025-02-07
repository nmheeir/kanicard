package com.nmheir.kanicard.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DownloadedDeckWithCards(
    @Embedded val deck: DownloadedDeckEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<DownloadedCardEntity>?
)
