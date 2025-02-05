package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.constants.SupabaseTable
import com.nmheir.kanicard.constants.SupabaseTable.DECKS
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import timber.log.Timber

class DeckRepo(
    private val postgrest: Postgrest,
    private val client: SupabaseClient
) : IDeckRepo {
    override suspend fun create(deck: DeckDto) {
        postgrest.from(DECKS).insert(deck)
    }

    override suspend fun delete(deck: DeckDto) {
        postgrest.from(DECKS).delete {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun update(deck: DeckDto) {
        postgrest.from(DECKS).update(deck) {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun getDeckDetail(deckId: Long): DeckDetailDto {
        val test = postgrest[DECKS]
            .select(
                /*Columns.raw(
                    "id, creator, title, description, thumbnail, created_at, last_updated, " +
                            "${SupabaseTable.PROFILE}(uid, user_name, email, avatar_url)"
                )*/
                Columns.raw(
                    "*, " +
                            "${SupabaseTable.PROFILE}(uid, user_name, email, avatar_url)"
                )
            ) {
                filter { eq("id", deckId) }
            }.decodeSingle<DeckDetailDto>()

        Timber.d(test.toString())
        return test
    }

    override suspend fun fetchMyDecks(): List<DeckDto> {
        val uid = client.auth.currentUserOrNull()?.id
        Timber.d(uid)
        return postgrest[DECKS]
            .select() {
                filter {
                    DeckDto::creator eq uid
                }
                order("id", Order.ASCENDING)
            }
            .decodeList<DeckDto>()
    }
}