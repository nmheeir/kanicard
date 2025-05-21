package com.nmheir.kanicard.di

import android.content.Context
import com.nmheir.kanicard.data.local.DatabaseDao
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.local.RoomCallBack
import com.nmheir.kanicard.data.repository.CardRepo
import com.nmheir.kanicard.data.repository.DeckRepo
import com.nmheir.kanicard.data.repository.NoteRepo
import com.nmheir.kanicard.data.repository.StatisticRepo
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        roomCallBack: RoomCallBack
    ): KaniDatabase {
        return InternalDatabase.newInstance(context, roomCallBack)
    }


    @Provides
    fun provideDatabaseDao(db: KaniDatabase): DatabaseDao {
        return db
    }
}