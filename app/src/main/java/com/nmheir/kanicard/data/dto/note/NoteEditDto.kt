package com.nmheir.kanicard.data.dto.note

import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.buildFieldJson
import java.time.LocalDateTime

data class NoteEditDto(
    val deckId: Long = 0,
    val typeId: Long = 0,
    val templateId: Long = 0,
    val field: Map<String, String> = emptyMap(),
    val createdTime: LocalDateTime = LocalDateTime.now(),
    val modifiedTime: LocalDateTime = LocalDateTime.now()
) {
    fun toNoteEntity() = NoteEntity(
        dId = deckId,
        templateId = templateId,
        fieldJson = buildFieldJson(field),
        createdTime = createdTime,
        modifiedTime = modifiedTime
    )
}
