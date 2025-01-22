package com.nmheir.kanicard.di

import android.content.Context
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
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



}