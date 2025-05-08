package com.nmheir.kanicard.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

data class NoteTypeWithFieldDefs(
    @Embedded val noteType: NoteTypeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteTypeId"
    )
    val fieldDefs: List<FieldDefEntity>
)
