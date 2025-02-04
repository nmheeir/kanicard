package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.DeckEntity
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

    val isLoading = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    val myDecks = MutableStateFlow<List<DeckEntity>?>(null)
    val importedDeck = MutableStateFlow<List<DeckEntity>?>(null)
    val allDecks = MutableStateFlow<List<DeckEntity>?>(null)

    val selectedHomeCategory = MutableStateFlow(HomeCategory.MY_DECK)

    init {
        isLoading.value = true
        viewModelScope.launch {
            load()
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

            val uid = client.auth.currentUserOrNull()?.id
            importedDeck.value = database.getImportedDecks(uid!!)
                .first().take(20)

            allDecks.value = myDecks.value.orEmpty() + importedDeck.value.orEmpty()
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
    IMPORTED_DECK
}

data class HomeUiState(
    val selectedHomeCategory: HomeCategory = HomeCategory.MY_DECK
)