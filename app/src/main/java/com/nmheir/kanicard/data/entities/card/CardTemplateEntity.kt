package com.nmheir.kanicard.data.entities.card

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity

@Entity(
    tableName = "card_templates",
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
data class CardTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteTypeId: Long,
    val name: String,
    val qstFt: String,
    val ansFt: String
)