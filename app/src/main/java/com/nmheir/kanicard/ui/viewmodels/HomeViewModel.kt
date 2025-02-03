package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.DeckEntity
import com.nmheir.kanicard.data.entities.User
import com.nmheir.kanicard.data.remote.repository.DeckRepo
import com.nmheir.kanicard.data.remote.repository.irepo.IDeckRepo
import com.nmheir.kanicard.domain.usecase.DeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deckUseCase: DeckUseCase,
    private val client: SupabaseClient
) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    val decks = MutableStateFlow<List<DeckEntity>?>(null)

    init {
/*        isLoading.value = true
        viewModelScope.launch {
            fetchMyDeck()
            isLoading.value = false
        }*/
    }

    fun onAction() {

    }

    private suspend fun fetchMyDeck() {
        try {
            decks.value = deckUseCase.fetchMyDeck()
//            decks.value = emptyList()
            Timber.d(decks.value.toString())
        } catch (e: Exception) {
            decks.value = emptyList()
            Timber.d(e)
        }
    }

    fun refresh() {
        Timber.d("refreshing")
        isRefreshing.value = true
        viewModelScope.launch {
            fetchMyDeck()
            isRefreshing.value = false
        }
    }

}

@Immutable
sealed class HomeAction {

}

sealed interface HomeScreenState {

}