package com.nmheir.kanicard.data.dto.deck

import androidx.room.Embedded
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity

data class DeckOptionUsageDto(
    @Embedded val option: DeckOptionEntity,
    val usage: Long
)
