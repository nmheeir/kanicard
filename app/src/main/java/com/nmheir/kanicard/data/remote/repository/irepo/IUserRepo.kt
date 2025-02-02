package com.nmheir.kanicard.data.remote.repository.irepo

import com.nmheir.kanicard.data.entities.Profile

interface IUserRepo {
    suspend fun fetchProfile(): Profile
    suspend fun updateProfile(profile: Profile)
}