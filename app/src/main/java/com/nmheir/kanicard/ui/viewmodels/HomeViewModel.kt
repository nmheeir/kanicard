package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.utils.fakeDeckWidgetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: KaniDatabase,
    private val deckRepo: IDeckRepo
) : ViewModel() {

    val isRefreshing = MutableStateFlow(false)

    val deckWidgetData = deckRepo.getAllDeckWidgetData()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        Timber.d(deckWidgetData.value.toString())
    }

    fun refresh() {
        isRefreshing.value = true
        viewModelScope.launch {
            delay(3000)
            isRefreshing.value = false
        }
    }
}