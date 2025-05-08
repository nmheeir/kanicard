package com.nmheir.kanicard.data.repository

import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IStatisticRepo
import timber.log.Timber
import javax.inject.Inject

class StatisticRepo @Inject constructor(
    private val database: KaniDatabase
) : IStatisticRepo {
    override fun test() {
        Timber.d("Test")
    }
}