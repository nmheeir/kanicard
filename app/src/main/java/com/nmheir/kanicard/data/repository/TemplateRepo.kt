package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import javax.inject.Inject

class TemplateRepo @Inject constructor(
    private val database: KaniDatabase
) : ITemplateRepo {
    override suspend fun insert(template: CardTemplateEntity) {
        database.insert(template)
    }
}