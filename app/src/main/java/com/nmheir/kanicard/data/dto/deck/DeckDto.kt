package com.nmheir.kanicard.data.dto.deck

import java.time.OffsetDateTime

data class DeckDto(
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime,
    val flags: Int
)