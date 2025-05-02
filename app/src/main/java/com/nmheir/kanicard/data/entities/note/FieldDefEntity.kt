package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "field_defs",
    foreignKeys = [
        ForeignKey(
            entity = NoteTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteTypeId")]
)
data class FieldDefEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteTypeId: Long,
    val name: String,
    val ord: Int,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime
)