package com.nmheir.kanicard.data.entities.card

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

@Entity(
    tableName = "templates",
    foreignKeys = [
        ForeignKey(
            entity = NoteTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["ntId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("ntId")]
)
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ntId: Long,
    val name: String,
    val qstFt: String,
    val ansFt: String
)