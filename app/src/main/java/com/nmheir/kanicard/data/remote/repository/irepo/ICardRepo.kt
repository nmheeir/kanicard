package com.nmheir.kanicard.data.remote.repository.irepo

import com.nmheir.kanicard.data.dto.CardDto

interface ICardRepo {

    suspend fun cardsByDeckId(deckId: Long, pageNumber: Int): List<CardDto>
}