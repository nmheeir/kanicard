package com.nmheir.kanicard.di

import android.content.Context
import androidx.compose.runtime.clearCompositionErrors
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.remote.repository.UserRepo
import com.nmheir.kanicard.data.remote.repository.irepo.IUserRepo
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
    fun provideUserRepo(postgrest: Postgrest, client: SupabaseClient): IUserRepo {
        return UserRepo(postgrest, client)
    }
}