package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.deck.DeckData
import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deckRepo: IDeckRepo,
    private val noteRepo: INoteRepo
) : ViewModel() {
    private val dId = savedStateHandle.get<Long>("deckId") ?: error("deckId is required")

    val deckData = MutableStateFlow<DeckData>(sampleDeckData)
    val noteData = MutableStateFlow<List<NoteData>>(emptyList())

    val isLoading = MutableStateFlow(false)

    init {
        Timber.d(dId.toString())
        isLoading.value = true
        viewModelScope.launch {
            load()
            isLoading.value = false
        }
    }

    fun onAction(action: DeckDetailUiAction) {
        when (action) {
            is DeckDetailUiAction.RenameDeck -> {
                renameDeck(action.newName)
            }

            is DeckDetailUiAction.UpdateDescription -> {
                viewModelScope.launch {
                    deckRepo.update(id = dId, description = action.newDescription)
                }
            }
        }
    }

    private fun load() {
        deckRepo.getDeckDataById(dId)
            .distinctUntilChanged()
            .onEach {
                deckData.value = it
            }
            .launchIn(viewModelScope)

        noteRepo.getNoteDataByDeckId(dId, false)
            .distinctUntilChanged()
            .onEach {
                noteData.value = it ?: emptyList()
            }
            .launchIn(viewModelScope)
    }

    private fun renameDeck(newName: String) {
        viewModelScope.launch {
            deckRepo.update(id = dId, name = newName)
        }
    }
}

sealed interface DeckDetailUiAction {
    data class RenameDeck(val newName: String) : DeckDetailUiAction
    data class UpdateDescription(val newDescription: String) : DeckDetailUiAction
}

private val sampleDeckData = DeckData(
    id = 0L,
    cId = 0L,
    name = "",
    description = "",
    noteCount = 0,
    createdTime = LocalDateTime.now(),
    modifiedTime = null
)