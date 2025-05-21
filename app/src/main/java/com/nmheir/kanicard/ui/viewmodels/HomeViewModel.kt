package com.nmheir.kanicard.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.CollectionWithDeckWidgetData
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.domain.repository.ICollectionRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deckRepo: IDeckRepo,
    private val collectionRepo: ICollectionRepo
) : ViewModel() {

    private val _channel = Channel<HomeUiEvent>()
    val channel = _channel.receiveAsFlow()

    val error = MutableStateFlow<String?>(null)

    val isRefreshing = MutableStateFlow(false)

    val collections = collectionRepo.getAllCollections()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val deckWidgetData = deckRepo.getAllDeckWidgetData()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val collectionWithDecks = collectionRepo.getAllCollectionWithDecks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val collectionWithWidgetDecksFlow =
        combine(deckWidgetData, collectionWithDecks) { widgets, collections ->
            collections.map { collection ->
                // Với mỗi collection, tìm widget data tương ứng cho mỗi deck của nó
                val widgetDecks = collection.decks.mapNotNull { deck ->
                    widgets.find { it.deckId == deck.id }
                }
                CollectionWithDeckWidgetData(
                    collection = collection.collection,
                    deckWidgetDatas = widgetDecks
                )
            }
        }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
//        Timber.d(collectionWithWidgetDecksFlow.value.toString())
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.UpdateDeckName -> {
                viewModelScope.launch {
                    deckRepo.updateName(action.id, action.name)
                }
            }

            is HomeUiAction.CreateNewCollection -> {
                viewModelScope.launch {
                    collectionRepo.insert(CollectionEntity(name = action.name))
                }
            }

            is HomeUiAction.CreateNewDeck -> {
                createNewDeck(action.name, action.collectionId)
            }

            is HomeUiAction.DeleteDeck -> {
                viewModelScope.launch {
                    deckRepo.deleteById(action.deckId)
                }
            }

            HomeUiAction.ErrorAccept -> {
                error.value = null
            }
        }
    }


    fun refresh() {
        isRefreshing.value = true
        viewModelScope.launch {
            delay(3000)
            isRefreshing.value = false
        }
    }

    private fun createNewDeck(name: String, collectionId: Long) {
        viewModelScope.launch {
            val existedDeck = deckRepo.queryDeck(name = name).first()
            Timber.d(existedDeck.toString())
            if (existedDeck != null) {
//                _channel.send(HomeUiEvent.Failure(R.string.alert_already_exist_deck))
                error.value = "Deck already exist"
                return@launch
            }

            val deckDto = DeckDto(
                name = name,
                description = "",
                colId = collectionId,
                createdTime = OffsetDateTime.now(),
            )

            deckRepo.insert(deckDto)
        }
    }
}

sealed interface HomeUiAction {
    data class UpdateDeckName(val id: Long, val name: String) : HomeUiAction
    data class CreateNewCollection(val name: String) : HomeUiAction
    data class CreateNewDeck(val name: String, val collectionId: Long) : HomeUiAction
    data class DeleteDeck(val deckId: Long) : HomeUiAction
    data object ErrorAccept : HomeUiAction
}

sealed interface HomeUiEvent {
    data class Success(val message: String) : HomeUiEvent
    data class Failure(@StringRes val msgRes: Int) : HomeUiEvent
}