package com.nmheir.kanicard.di

import android.content.Context
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.repository.CardRepo
import com.nmheir.kanicard.data.repository.DeckRepo
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KaniDatabase {
        return InternalDatabase.newInstance(context)
    }

    @Provides
    @Singleton
    fun provideDeckRepo(): IDeckRepo {
        return DeckRepo()
    }

    @Provides
    @Singleton
    fun provideCardRepo(): ICardRepo {
        return CardRepo()
    }
}