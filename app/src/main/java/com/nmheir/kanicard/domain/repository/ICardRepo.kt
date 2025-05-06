package com.nmheir.kanicard.domain.repository

import com.nmheir.kanicard.data.dto.CardDto

interface ICardRepo {

    suspend fun cardsByDeckId(deckId: Long, pageNumber: Int): List<CardDto>
}