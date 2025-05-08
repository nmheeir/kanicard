package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity

data class NoteAndTemplate(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "templateId",
        entityColumn = "id"
    )
    val template: CardTemplateEntity
)
