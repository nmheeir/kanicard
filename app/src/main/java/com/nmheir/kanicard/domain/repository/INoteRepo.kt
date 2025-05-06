package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.entities.note.NoteEntity

interface INoteRepo {
    suspend fun getNoteByNoteId(noteId: Long): NoteDto?
    suspend fun insert(note: NoteEntity)
    suspend fun update(note: NoteEntity)
    suspend fun delete(noteId: Long)
}