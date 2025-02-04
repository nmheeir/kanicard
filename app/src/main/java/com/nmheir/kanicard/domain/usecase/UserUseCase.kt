package com.nmheir.kanicard.domain.usecase

import com.nmheir.kanicard.data.entities.ProfileEntity
import com.nmheir.kanicard.data.remote.repository.irepo.IUserRepo

data class UserUseCase(
    val fetchProfile: FetchProfile
)

class FetchProfile(
    private val iUserRepo: IUserRepo
) {
    suspend operator fun invoke() : ProfileEntity {
        return iUserRepo.fetchProfile()
    }
}
