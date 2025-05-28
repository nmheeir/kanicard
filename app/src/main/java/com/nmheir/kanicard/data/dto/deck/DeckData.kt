package com.nmheir.kanicard.data.dto.deck

import java.time.LocalDateTime

data class DeckData(
    val id: Long,
    val cId: Long,                  // Collection Id
    val name: String,
    val description: String,
    val noteCount: Int,
    val createdTime: LocalDateTime,
    val modifiedTime: LocalDateTime?,
)
