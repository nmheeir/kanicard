package com.nmheir.kanicard.di

import com.nmheir.kanicard.data.repository.CollectionRepo
import com.nmheir.kanicard.data.repository.DeckRepo
import com.nmheir.kanicard.data.repository.FieldRepo
import com.nmheir.kanicard.data.repository.StatisticRepo
import com.nmheir.kanicard.data.repository.TemplateRepo
import com.nmheir.kanicard.domain.repository.ICollectionRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.IFieldRepo
import com.nmheir.kanicard.domain.repository.IStatisticRepo
import com.nmheir.kanicard.domain.repository.ITemplateRepo
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

    @Binds
    fun bindCollectionRepo(collectionRepo: CollectionRepo): ICollectionRepo

    @Binds
    fun bindFieldRepo(fieldRepo: FieldRepo): IFieldRepo

    @Binds
    fun bindTemplateRepo(templateRepo: TemplateRepo): ITemplateRepo

    @Binds
    fun bindDeckRepo(deckRepo: DeckRepo): IDeckRepo
}