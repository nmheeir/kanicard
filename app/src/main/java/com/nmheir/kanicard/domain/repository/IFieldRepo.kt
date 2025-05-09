package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import kotlinx.coroutines.flow.Flow

interface IFieldRepo {
    fun getFieldsByNoteTypeId(noteTypeId: Long): Flow<List<FieldDefEntity>?>
    suspend fun inserts(fields: List<FieldDefEntity>)
}