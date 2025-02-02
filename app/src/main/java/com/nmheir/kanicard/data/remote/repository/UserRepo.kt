package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.data.entities.Profile
import com.nmheir.kanicard.data.remote.repository.irepo.IUserRepo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate
import timber.log.Timber

class UserRepo(
    private val postgrest: Postgrest,
    private val client: SupabaseClient
) : IUserRepo {
    override suspend fun fetchProfile(): Profile {
        val uid = client.auth.currentUserOrNull()?.id
        val profile = postgrest.from("profiles")
            .select() {
                filter {
                    eq("uid", uid!!)
                }
            }
            .decodeSingle<Profile>()
        Timber.d(profile.toString())
        return profile
    }

    override suspend fun updateProfile(profile: Profile) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid != null) {
            postgrest.from("profiles").update(
                {
                    Profile::userName setTo profile.userName
                    Profile::avatarUrl setTo profile.avatarUrl
                    Profile::updatedAt setTo LocalDate.toString()
                    Profile::bio setTo profile.bio
                }
            ) {
                filter {
                    eq("uid", uid)
                }
            }
        }
    }
}