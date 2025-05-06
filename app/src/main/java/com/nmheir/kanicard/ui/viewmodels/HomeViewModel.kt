package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.data.repository.DeckRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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

    }

    fun refresh() {

    }
}

sealed interface HomeAction {
    data class HomeCategorySelected(val category: HomeCategory) : HomeAction
}

enum class HomeCategory {
    ALL,
    MY_DECK,
    DOWNLOADED
}

data class HomeUiState(
    val selectedHomeCategory: HomeCategory = HomeCategory.MY_DECK
)