package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import kotlinx.coroutines.flow.Flow

interface ITemplateRepo {
    suspend fun insert(template: CardTemplateEntity)
    suspend fun inserts(templates: List<CardTemplateEntity>)
    suspend fun delete(id: Long)

    fun getTemplatesByNoteTypeId(noteTypeId: Long): Flow<List<CardTemplateEntity>?>
}