package com.nmheir.kanicard.data.dto.note

import java.time.LocalDateTime

data class NoteDto(
    val id: Long,
    val tId: Long,
    val field: Map<String, String>,
    val createdTime: LocalDateTime,
    val modifiedTime: LocalDateTime
)