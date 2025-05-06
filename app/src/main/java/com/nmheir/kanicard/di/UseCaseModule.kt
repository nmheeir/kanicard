package com.nmheir.kanicard.di

import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.usecase.CardUseCase
import com.nmheir.kanicard.domain.usecase.GetCardsByDeckId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

//    @Provides
//    @Singleton
//    fun provideDeckUseCase(iDeckRepo: IDeckRepo): DeckUseCase {
//        return DeckUseCase(
//        )
//    }

    @Provides
    @Singleton
    fun provideCardUseCase(iCardRepo: ICardRepo): CardUseCase {
        return CardUseCase(
            getCardsByDeckId = GetCardsByDeckId(iCardRepo)
        )
    }
}