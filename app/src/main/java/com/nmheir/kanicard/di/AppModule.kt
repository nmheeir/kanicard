package com.nmheir.kanicard.di

import android.content.Context
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.di.repository.CardRepo
import com.nmheir.kanicard.di.repository.DeckRepo
import com.nmheir.kanicard.di.repository.irepo.ICardRepo
import com.nmheir.kanicard.di.repository.irepo.IDeckRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
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