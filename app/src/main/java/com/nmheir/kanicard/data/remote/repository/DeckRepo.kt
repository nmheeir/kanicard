package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.constants.SupabaseTable.DECKS
import com.nmheir.kanicard.constants.SupabaseTable.PROFILE
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import timber.log.Timber

class DeckRepo(
    private val postgrest: Postgrest,
    private val client: SupabaseClient
) : IDeckRepo {
    override suspend fun create(deck: DeckEntity) {
        postgrest.from(DECKS).insert(deck)
    }

    override suspend fun delete(deck: DeckEntity) {
        postgrest.from(DECKS).delete {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun update(deck: DeckEntity) {
        postgrest.from(DECKS).update(deck) {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun getDeckDetail(deckId: Long): DeckDetailDto {
        val test = postgrest[DECKS]
            .select(
                Columns.raw(
                    "id, creator, title, description, thumbnail, created_at, " +
                            "${PROFILE}(user_name, email, avatar_url)"
                )
            ) {
                filter { eq("id", deckId) }
            }.decodeSingle<DeckDetailDto>()

        Timber.d(test.toString())
        return test
    }

    override suspend fun fetchMyDecks(): List<DeckEntity> {
        val uid = client.auth.currentUserOrNull()?.id
        Timber.d(uid)
        return postgrest[DECKS]
            .select() {
                filter {
                    DeckEntity::creator eq uid
                }
            }
            .decodeList<DeckEntity>()
    }
}