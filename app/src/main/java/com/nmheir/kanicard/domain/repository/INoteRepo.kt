package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.dto.note.NoteEditDto
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.relations.NoteTypeWithFieldDefs
import com.nmheir.kanicard.data.relations.NoteTypeWithTemplates
import kotlinx.coroutines.flow.Flow

interface INoteRepo {
    suspend fun getNoteByNoteId(noteId: Long): NoteDto?
    fun getAllNoteTypes(): Flow<List<NoteTypeEntity>?>
    suspend fun getNoteTypeWithTemplates(noteTypeId: Long): NoteTypeWithTemplates?
    suspend fun getNoteTypeWithFieldDefs(noteTypeId: Long): NoteTypeWithFieldDefs?

    suspend fun insert(note: NoteEditDto)
    suspend fun insert(noteType: NoteTypeEntity) : Long

    suspend fun update(note: NoteEntity)
    suspend fun delete(noteId: Long)
}