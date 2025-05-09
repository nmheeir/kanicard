package com.nmheir.kanicard.data.dto.note

import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.buildFieldJson
import java.time.OffsetDateTime

data class NoteEditDto(
    val deckId: Long,
    val templateId: Long,
    val field: Map<String, String>,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime
) {
    fun toNoteEntity() = NoteEntity(
        deckId = deckId,
        templateId = templateId,
        fieldJson = buildFieldJson(field),
        createdTime = createdTime,
        modifiedTime = modifiedTime
    )
}
