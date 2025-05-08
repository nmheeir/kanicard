package com.nmheir.kanicard.ui.viewmodels

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.R
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.domain.repository.ICollectionRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    val isRefreshing = MutableStateFlow(false)

    val deckWidgetData = deckRepo.getAllDeckWidgetData()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val collections = deckRepo.getAllCollections()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        Timber.d(deckWidgetData.value.toString())
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
            val existedDeck = deckRepo.queryDeck(name = name)
            if (existedDeck != null) {
                _channel.send(HomeUiEvent.Failure(R.string.alert_already_exist_deck))
                return@launch
            }

            val deckDto = DeckDto(
                name = name,
                description = "",
                collectionId = collectionId,
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
}

sealed interface HomeUiEvent {
    data class Success(val message: String) : HomeUiEvent
    data class Failure(@StringRes val msgRes: Int) : HomeUiEvent
}