package com.nmheir.kanicard.data.entities.deck

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import java.time.OffsetDateTime

@Entity(
    tableName = "decks",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["colId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeckOptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["oId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index("name", unique = true)]
)
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(defaultValue = "1")
    val oId: Long = 1L,
    val colId: Long,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime = OffsetDateTime.now(),
    val modifiedTime: OffsetDateTime? = null,
    val flags: Int? = null
) {
    fun toDeckDto(): DeckDto {
        return DeckDto(
            id = id,
            colId = colId,
            name = name,
            description = description,
            createdTime = createdTime,
            modifiedTime = modifiedTime,
            flags = flags
        )
    }
}