package com.nmheir.kanicard.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nmheir.kanicard.data.dto.DeckDto

@Entity(tableName = "downloaded_decks")
data class DownloadedDeckEntity(
    @PrimaryKey val id: Long,
    val creator: String,
    val title: String,
    val thumbnail: String ?= null,
    val description: String ?= null,
    val createdAt: String,
    val userId: String,
    val lastUpdated: String
) {
    fun toDeckDto(): DeckDto {
        return DeckDto(
            id = id,
            creator = creator,
            title = title,
            thumbnail = thumbnail,
            description = description,
            createdAt = createdAt,
            lastUpdated = lastUpdated
        )
    }
}

fun List<DownloadedDeckEntity>?.toDeckDtoList(): List<DeckDto> {
    if (this.isNullOrEmpty()) return emptyList()
    return map { it.toDeckDto() }
}
