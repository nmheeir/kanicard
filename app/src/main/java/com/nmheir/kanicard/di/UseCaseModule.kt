package com.nmheir.kanicard.di

import com.nmheir.kanicard.data.remote.repository.irepo.ICardRepo
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo
import com.nmheir.kanicard.data.remote.repository.irepo.IUserRepo
import com.nmheir.kanicard.domain.usecase.CardUseCase
import com.nmheir.kanicard.domain.usecase.CreateDeck
import com.nmheir.kanicard.domain.usecase.DeckUseCase
import com.nmheir.kanicard.domain.usecase.FetchMyDeck
import com.nmheir.kanicard.domain.usecase.FetchProfile
import com.nmheir.kanicard.domain.usecase.GetCardsByDeckId
import com.nmheir.kanicard.domain.usecase.GetDeckDetail
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

    @Provides
    @Singleton
    fun provideDeckUseCase(iDeckRepo: IDeckRepo): DeckUseCase {
        return DeckUseCase(
            create = CreateDeck(iDeckRepo),
            fetchMyDeck = FetchMyDeck(iDeckRepo),
            getDeckDetail = GetDeckDetail(iDeckRepo)
        )
    }

    @Provides
    @Singleton
    fun provideCardUseCase(iCardRepo: ICardRepo): CardUseCase {
        return CardUseCase(
            getCardsByDeckId = GetCardsByDeckId(iCardRepo)
        )
    }
}