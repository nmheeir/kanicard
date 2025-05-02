package com.nmheir.kanicard.di.repository

import com.nmheir.kanicard.constants.SupabaseTable
import com.nmheir.kanicard.constants.SupabaseTable.DECKS
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.di.repository.irepo.IDeckRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import timber.log.Timber

class DeckRepo(
) : IDeckRepo {

}