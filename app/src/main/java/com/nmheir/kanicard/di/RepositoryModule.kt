package com.nmheir.kanicard.di

import com.nmheir.kanicard.data.repository.StatisticRepo
import com.nmheir.kanicard.domain.repository.IStatisticRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// TODO: Test @Bind in Dagger Hilt

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindStatisticRepo(statisticRepo: StatisticRepo): IStatisticRepo
}