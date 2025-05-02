package com.nmheir.kanicard.di.repository

import com.nmheir.kanicard.constants.SupabaseTable
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.di.repository.irepo.ICardRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order

class CardRepo(
) : ICardRepo {
    override suspend fun cardsByDeckId(
        deckId: Long,
        pageNumber: Int
    ): List<CardDto> {
        return emptyList()
    }

}