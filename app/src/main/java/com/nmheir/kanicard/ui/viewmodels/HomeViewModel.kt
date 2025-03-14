package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.entities.DownloadedDeckEntity
import com.nmheir.kanicard.data.entities.toDeckDtoList
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.usecase.DeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deckUseCase: DeckUseCase,
    private val client: SupabaseClient,
    private val database: KaniDatabase
) : ViewModel() {
    private val uid = client.auth.currentUserOrNull()?.id

    val isLoading = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    val myDecks = MutableStateFlow<List<DeckDto>?>(null)
    val downloadedDeck = MutableStateFlow<List<DownloadedDeckEntity>?>(null)
    val allDecks = MutableStateFlow<List<DeckDto>?>(null)

    val selectedHomeCategory = MutableStateFlow(HomeCategory.MY_DECK)

    init {
        isLoading.value = true
        viewModelScope.launch {
//            load()
            Timber.d(client.auth.currentUserOrNull().toString())
            isLoading.value = false
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.HomeCategorySelected -> onHomeCategorySelected(action.category)
        }
    }

    private suspend fun load() {
        try {
            myDecks.value = deckUseCase.fetchMyDeck()

            downloadedDeck.value = database.getDownloadedDecks(uid!!)
                .first().take(20)

            allDecks.value =
                (myDecks.value.orEmpty() + downloadedDeck.value.toDeckDtoList()).distinctBy { it.id }
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    private fun onHomeCategorySelected(category: HomeCategory) {
        selectedHomeCategory.value = category
    }

    fun refresh() {
        isRefreshing.value = true
        viewModelScope.launch {
            load()
            isRefreshing.value = false
        }
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