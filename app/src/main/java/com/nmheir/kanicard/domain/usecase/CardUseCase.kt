package com.nmheir.kanicard.domain.usecase

import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.domain.repository.ICardRepo

data class CardUseCase(
    val getCardsByDeckId: GetCardsByDeckId
)

class GetCardsByDeckId(
    private val iCardRepo: ICardRepo
) {
    suspend operator fun invoke(deckId: Long, pageNumber: Int): List<CardDto> {
        return iCardRepo.cardsByDeckId(deckId, pageNumber)
    }
}
