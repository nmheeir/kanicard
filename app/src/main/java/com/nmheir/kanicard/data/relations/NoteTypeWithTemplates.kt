package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.card.TemplateEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

data class NoteTypeWithTemplates(
    @Embedded val noteType: NoteTypeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ntId"
    )
    val templates: List<TemplateEntity>
)