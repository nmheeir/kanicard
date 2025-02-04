package com.nmheir.kanicard.data.remote.repository.irepo

import com.nmheir.kanicard.data.entities.ProfileEntity

interface IUserRepo {
    suspend fun fetchProfile(): ProfileEntity
    suspend fun updateProfile(profileEntity: ProfileEntity)
}