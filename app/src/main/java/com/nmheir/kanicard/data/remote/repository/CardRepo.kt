package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.constants.SupabaseTable
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.remote.repository.irepo.ICardRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order

class CardRepo(
    private val client: SupabaseClient,
    private val postgrest: Postgrest
) : ICardRepo {
    override suspend fun cardsByDeckId(
        deckId: Long,
        pageNumber: Int
    ): List<CardDto> {
        val from = (pageNumber * PAGING_SIZE).toLong()
        val to = ((pageNumber + 1) * PAGING_SIZE).toLong()
        return postgrest[SupabaseTable.CARDS].select() {
            filter {
                eq("deck_id", deckId)
            }
            order("id", Order.ASCENDING)
            range(from, to)
        }.decodeList<CardDto>()
    }

    companion object {
        const val PAGING_SIZE = 20
    }
}