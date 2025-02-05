package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.entities.DownloadedDeckEntity
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.domain.usecase.CardUseCase
import com.nmheir.kanicard.domain.usecase.DeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    private val client: SupabaseClient,
    private val database: KaniDatabase,
    private val deckUseCase: DeckUseCase,
    private val cardUseCase: CardUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val deckId = savedStateHandle.get<Long>("deckId")!!
    private val uid = client.auth.currentUserOrNull()?.id

    val isLoading = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)
    val continuation = MutableStateFlow(true)

    val cards = MutableStateFlow<List<CardDto>?>(null)
    val deckDetail = MutableStateFlow<DeckDetailDto?>(null)
    val isDeckImported = MutableStateFlow<Boolean?>(null)

    private val pageNumber = MutableStateFlow(0)

    fun onAction(action: DeckDetailAction) {
        when (action) {
            is DeckDetailAction.ImportDeck -> downloadDeck(action.deckDto)
        }
    }

    init {
        isLoading.value = true
        viewModelScope.launch {
            load()
            isLoading.value = false
        }
    }

    private suspend fun load() {
        Timber.d("load")
        coroutineScope {
            val deckDetailDeferred =
                async(Dispatchers.IO) { deckUseCase.getDeckDetail(deckId) }
            val cardsDeferred =
                async(Dispatchers.IO) { cardUseCase.getCardsByDeckId(deckId, pageNumber.value) }

            deckDetail.value = deckDetailDeferred.await()
            cards.value = cardsDeferred.await()

        }
    }

    private fun downloadDeck(deck: DeckDto) {
        try {
            val downloadDeck = DownloadedDeckEntity(
                id = deck.id,
                creator = deck.creator,
                title = deck.title,
                thumbnail = deck.thumbnail,
                description = deck.description,
                createdAt = deck.createdAt,
                userId = uid!!,
                lastUpdated = deck.lastUpdated,
                totalCard = deck.totalCard,
                isPublic = deck.isPublic
            )
            database.insert(downloadDeck)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    private fun isDeckImported(): Boolean {
        val uid = client.auth.currentUserOrNull()?.id
        return database.getImportedDeckByID(uid!!, deckId) != null
    }

    fun loadMore() {
        viewModelScope.launch {
            Timber.d("load more")
            withContext(Dispatchers.IO) {
                delay(5000)
                pageNumber.value++
                val newCard = cardUseCase.getCardsByDeckId(deckId, pageNumber.value)
                if (newCard.isEmpty()) {
                    continuation.value = false
                    return@withContext
                } else {
                    cards.value = cards.value?.plus(newCard)?.distinctBy { it.id }
                }
            }
        }
    }

    fun refresh() {
        isRefreshing.value = true
        viewModelScope.launch {
            delay(5000)
            Timber.d("refresh")
            isRefreshing.value = false
        }
    }
}

sealed interface DeckDetailAction {
    data class ImportDeck(val deckDto: DeckDto) : DeckDetailAction
}