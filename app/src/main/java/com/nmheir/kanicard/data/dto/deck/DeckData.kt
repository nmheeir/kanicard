package com.nmheir.kanicard.data.dto.deck

import java.time.OffsetDateTime

data class DeckData(
    val id: Long,
    val cId: Long,                  // Collection Id
    val name: String,
    val description: String,
    val noteCount: Int,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime?,
)
