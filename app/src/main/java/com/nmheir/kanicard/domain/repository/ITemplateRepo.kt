package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.card.TemplateEntity
import kotlinx.coroutines.flow.Flow

interface ITemplateRepo {
    suspend fun insert(template: TemplateEntity)
    suspend fun inserts(templates: List<TemplateEntity>)
    suspend fun delete(id: Long)

    fun getTemplatesByNoteTypeId(noteTypeId: Long): Flow<List<TemplateEntity>?>
}