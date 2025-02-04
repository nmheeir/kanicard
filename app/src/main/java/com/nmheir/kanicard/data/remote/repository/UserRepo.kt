package com.nmheir.kanicard.data.remote.repository

import com.nmheir.kanicard.data.entities.ProfileEntity
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
    override suspend fun fetchProfile(): ProfileEntity {
        val uid = client.auth.currentUserOrNull()?.id
        val profileEntity = postgrest.from("profiles")
            .select() {
                filter {
                    eq("uid", uid!!)
                }
            }
            .decodeSingle<ProfileEntity>()
        Timber.d(profileEntity.toString())
        return profileEntity
    }

    override suspend fun updateProfile(profileEntity: ProfileEntity) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid != null) {
            postgrest.from("profiles").update(
                {
                    ProfileEntity::userName setTo profileEntity.userName
                    ProfileEntity::avatarUrl setTo profileEntity.avatarUrl
                    ProfileEntity::updatedAt setTo LocalDate.toString()
                    ProfileEntity::bio setTo profileEntity.bio
                }
            ) {
                filter {
                    eq("uid", uid)
                }
            }
        }
    }
}