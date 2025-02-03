package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import timber.log.Timber

class DeckRepo(
    private val postgrest: Postgrest,
    private val client: SupabaseClient
) : IDeckRepo {
    override suspend fun create(deck: DeckEntity) {
        postgrest.from(DECK_TABLE).insert(deck)
    }

    override suspend fun delete(deck: DeckEntity) {
        postgrest.from(DECK_TABLE).delete {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun update(deck: DeckEntity) {
        postgrest.from(DECK_TABLE).update(deck) {
            filter {
                eq("id", deck.id)
            }
        }
    }

    override suspend fun fetchMyDecks(): List<DeckEntity> {
        val uid = client.auth.currentUserOrNull()?.id
        Timber.d(uid)
        return postgrest[DECK_TABLE]
            .select() {
                filter {
                    DeckEntity::userId eq uid
                }
            }
            .decodeList<DeckEntity>()
    }
}

private const val DECK_TABLE = "decks"