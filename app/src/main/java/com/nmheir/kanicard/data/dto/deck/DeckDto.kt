package com.nmheir.kanicard.data.dto.deck

import com.nmheir.kanicard.data.entities.deck.DeckEntity
import java.time.OffsetDateTime

data class DeckDto(
    val id: Long = 0,
    val collectionId: Long = 0,
    val name: String,
    val description: String,
    val createdTime: OffsetDateTime,
    val modifiedTime: OffsetDateTime? = null,
    val flags: Int? = null
) {
    fun toDeckEntity(): DeckEntity {
        return DeckEntity(
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