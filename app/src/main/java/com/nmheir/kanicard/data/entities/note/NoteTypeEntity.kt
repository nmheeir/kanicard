package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

// TODO: type name should be unique
@Entity(tableName = "note_types")
data class NoteTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime,
    // TODO: modifiedTime can be null if not modified
    val modifiedTime: OffsetDateTime
)