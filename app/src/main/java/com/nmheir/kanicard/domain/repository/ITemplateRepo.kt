package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.entities.card.CardTemplateEntity

interface ITemplateRepo {
    suspend fun insert(template: CardTemplateEntity)
}