package com.nmheir.kanicard.data.dto

import com.nmheir.kanicard.data.entities.ProfileEntity

data class DeckDetailDto(
    val deckDto: DeckDto,
    val profileDto: ProfileDto,
) {
    fun toDeck(): DeckDto = deckDto

    fun toProfile(): ProfileDto = profileDto
}
