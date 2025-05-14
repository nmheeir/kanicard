package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TemplateRepo @Inject constructor(
    private val database: KaniDatabase
) : ITemplateRepo {
    override suspend fun insert(template: CardTemplateEntity) {
        database.insert(template)
    }

    override suspend fun inserts(templates: List<CardTemplateEntity>) {
        database.insertTemplates(templates)
    }

    override suspend fun delete(id: Long) {
        database.deleteTemplate(id)
    }

    override fun getTemplatesByNoteTypeId(noteTypeId: Long): Flow<List<CardTemplateEntity>?> {
        return database.getCardTemplateByNoteTypeId(noteTypeId)
    }
}