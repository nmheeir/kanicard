package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.note.FieldEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IFieldRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FieldRepo @Inject constructor(
    private val database: KaniDatabase
) : IFieldRepo {
    override fun getFieldsByNoteTypeId(noteTypeId: Long): Flow<List<FieldEntity>?> {
        return database.getFieldDefByNoteTypeId(noteTypeId)
    }

    override suspend fun inserts(fields: List<FieldEntity>) {
        database.insertFields(fields)
    }
}