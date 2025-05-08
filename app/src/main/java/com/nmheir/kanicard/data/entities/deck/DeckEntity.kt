package com.nmheir.kanicard.data.entities.deck

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.dto.deck.DeckDto
import java.time.OffsetDateTime

@Entity(
    tableName = "decks",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("name", unique = true)]
)
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime = OffsetDateTime.now(),
    val modifiedTime: OffsetDateTime? = null,
    val flags: Int? = null
) {
    fun toDeckDto() : DeckDto {
        return DeckDto(
            id = id,
            collectionId = collectionId,
            name = name,
            description = description,
            createdTime = createdTime,
            modifiedTime = modifiedTime,
            flags = flags
        )
    }
}