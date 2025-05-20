package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.dto.note.NoteEditDto
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.relations.NoteTypeWithFieldDefs
import com.nmheir.kanicard.data.relations.NoteTypeWithTemplates
import kotlinx.coroutines.flow.Flow

interface INoteRepo {
    fun getNoteByNoteId(noteId: Long): Flow<NoteDto?>
    fun getAllNoteTypes(): Flow<List<NoteTypeEntity>?>
    fun getNoteTypeWithTemplates(noteTypeId: Long): Flow<NoteTypeWithTemplates?>
    fun getNoteTypeWithFieldDefs(noteTypeId: Long): Flow<NoteTypeWithFieldDefs?>
    fun getNoteDataByDeckId(deckId: Long, parseDataToHtml: Boolean = true): Flow<List<NoteData>?>
    fun getNoteDataByNoteIds(nIds: List<Long>): Flow<List<NoteData>?>

    suspend fun insert(note: NoteEditDto)
    suspend fun insert(noteType: NoteTypeEntity): Long
    suspend fun insert(note: NoteEntity): Long
    suspend fun inserts(notes: List<NoteEntity>)

    suspend fun update(note: NoteEntity)
    suspend fun update(type: NoteTypeEntity)
    suspend fun delete(noteId: Long)
}