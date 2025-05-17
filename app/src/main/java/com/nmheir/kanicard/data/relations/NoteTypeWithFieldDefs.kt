package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.note.FieldEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

data class NoteTypeWithFieldDefs(
    @Embedded val noteType: NoteTypeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ntId"
    )
    val fieldDefs: List<FieldEntity>
)
