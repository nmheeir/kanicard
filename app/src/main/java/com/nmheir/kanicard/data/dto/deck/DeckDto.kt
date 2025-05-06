package com.nmheir.kanicard.data.dto.deck

import com.nmheir.kanicard.data.entities.deck.DeckEntity
import java.time.OffsetDateTime

data class DeckDto(
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime,
    val flags: Int
) {
    fun toDeckEntity(): DeckEntity {
        return DeckEntity(
            id = id,
            name = name,
            description = description,
            createdTime = createdTime,
            modifiedTime = modifiedTime,
            flags = flags
        )
    }
}