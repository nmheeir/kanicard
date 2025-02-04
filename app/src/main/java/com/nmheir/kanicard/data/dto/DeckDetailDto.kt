package com.nmheir.kanicard.data.dto

import com.nmheir.kanicard.data.entities.ProfileEntity

data class DeckDetailDto(
    val deckDto: DeckDto,
    val profileEntity: ProfileEntity,
) {
    fun toDeckDetail(): DeckDto = deckDto

    fun toProfile(): ProfileEntity = profileEntity
}
