package com.nmheir.kanicard.di

import com.nmheir.kanicard.data.remote.repository.irepo.IUserRepo
import com.nmheir.kanicard.domain.usecase.FetchProfile
import com.nmheir.kanicard.domain.usecase.UserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideUserUseCase(iUserRepo: IUserRepo): UserUseCase {
        return UserUseCase(fetchProfile = FetchProfile(iUserRepo))
    }
}