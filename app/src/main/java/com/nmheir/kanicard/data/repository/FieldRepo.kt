package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IFieldRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FieldRepo @Inject constructor(
    private val database: KaniDatabase
) : IFieldRepo {
    override fun getFieldsByNoteTypeId(noteTypeId: Long): Flow<List<FieldDefEntity>?> {
        return flow { emptyList<FieldDefEntity>() }
    }

    override suspend fun inserts(fields: List<FieldDefEntity>) {
        database.insertFields(fields)
    }
}