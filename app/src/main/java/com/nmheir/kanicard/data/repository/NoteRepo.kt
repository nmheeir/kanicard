package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.dto.note.NoteEditDto
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.entities.note.parseFieldJson
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.relations.NoteTypeWithFieldDefs
import com.nmheir.kanicard.data.relations.NoteTypeWithTemplates
import com.nmheir.kanicard.domain.repository.INoteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.mapNotNull

class NoteRepo(
    private val database: KaniDatabase
) : INoteRepo {
    override suspend fun getNoteByNoteId(noteId: Long): NoteDto? {
        val noteEntity = database.getNoteByNoteId(noteId)
            .mapNotNull {
                it?.let { note ->
                    NoteDto(
                        id = note.noteId,
                        field = parseFieldJson(note.fieldJson),
                        createdTime = note.createdTime,
                        modifiedTime = note.modifiedTime
                    )
                }
            }
        return noteEntity.lastOrNull()
    }

    override fun getAllNoteTypes(): Flow<List<NoteTypeEntity>?> {
        return database.getAllNoteTypes()
    }

    override fun getNoteTypeWithTemplates(noteTypeId: Long): Flow<NoteTypeWithTemplates?> {
        return database.getNoteTypesWithTemplates(noteTypeId)
    }

    override fun getNoteTypeWithFieldDefs(noteTypeId: Long): Flow<NoteTypeWithFieldDefs?> {
        return database.getNoteTypeWithFieldDef(noteTypeId)
    }

    override suspend fun insert(note: NoteEditDto) {
        database.insert(note.toNoteEntity())
    }

    override suspend fun insert(noteType: NoteTypeEntity) : Long {
        return database.insert(noteType)
    }

    override suspend fun update(note: NoteEntity) {
        database.update(note)
    }

    override suspend fun update(type: NoteTypeEntity) {
        database.update(type)
    }

    override suspend fun delete(noteId: Long) {
        database.deleteNote(noteId)
    }

}