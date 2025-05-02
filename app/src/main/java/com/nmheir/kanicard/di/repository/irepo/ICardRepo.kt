package com.nmheir.kanicard.di.repository.irepo

import com.nmheir.kanicard.data.dto.CardDto

interface ICardRepo {

    suspend fun cardsByDeckId(deckId: Long, pageNumber: Int): List<CardDto>
}