package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

data class NoteTypeWithTemplates(
    @Embedded val noteType: NoteTypeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteTypeId"
    )
    val templates: List<CardTemplateEntity>
)