package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.domain.repository.ICardRepo

class CardRepo(
) : ICardRepo {
    override suspend fun cardsByDeckId(
        deckId: Long,
        pageNumber: Int
    ): List<CardDto> {
        return emptyList()
    }

}