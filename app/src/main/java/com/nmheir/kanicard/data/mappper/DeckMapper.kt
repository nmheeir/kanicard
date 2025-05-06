package com.nmheir.kanicard.data.mappper

import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.entities.deck.DeckEntity
import com.nmheir.kanicard.domain.mapper.Mapper

class DeckMapper : Mapper<DeckDto, DeckEntity> {
    override fun to(value: DeckDto): DeckEntity =
        DeckEntity(
            id = value.id,
            name = value.name,
            description = value.description,
            createdTime = value.createdTime,
            modifiedTime = value.modifiedTime,
            flags = value.flags
        )

    override fun from(value: DeckEntity): DeckDto =
        DeckDto(
            id = value.id,
            name = value.name,
            description = value.description,
            createdTime = value.createdTime,
            modifiedTime = value.modifiedTime,
            flags = value.flags
        )
}
