package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "notes",
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
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val noteTypeId: Long,
    val fieldJson: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime
)