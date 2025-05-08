package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.note.NoteDto
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.parseFieldJson
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.INoteRepo
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

    override suspend fun insert(note: NoteEntity) {
        database.insert(note)
    }

    override suspend fun update(note: NoteEntity) {
        database.update(note)
    }

    override suspend fun delete(noteId: Long) {
        database.deleteNote(noteId)
    }

}