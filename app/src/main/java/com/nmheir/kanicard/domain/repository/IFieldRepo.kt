package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.note.FieldEntity
import kotlinx.coroutines.flow.Flow

interface IFieldRepo {
    fun getFieldsByNoteTypeId(noteTypeId: Long): Flow<List<FieldEntity>?>
    suspend fun inserts(fields: List<FieldEntity>)
}