package com.nmheir.kanicard.data.entities.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = NoteTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["ntId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ntId")]
)
data class FieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ntId: Long,
    val name: String,
    val ord: Int,
    val createdTime: LocalDateTime,
    val modifiedTime: LocalDateTime
)