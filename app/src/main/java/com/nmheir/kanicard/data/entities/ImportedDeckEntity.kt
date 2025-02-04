package com.nmheir.kanicard.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imported_decks")
data class ImportedDeckEntity(
    @PrimaryKey val id: Long,
    val creator: String,
    val title: String,
    val thumbnail: String,
    val description: String,
    val createdAt: String,
    val userId: String,
)
