package com.nmheir.kanicard.data.dto.note

import java.time.OffsetDateTime

data class NoteDto(
    val id: Long,
    val tId: Long,
    val field: Map<String, String>,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime
)