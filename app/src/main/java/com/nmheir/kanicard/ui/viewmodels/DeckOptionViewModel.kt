@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.option.DeckOptionEntity
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.domain.repository.IDeckOptionRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class DeckOptionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deckRepo: IDeckRepo,
    private val optionRepo: IDeckOptionRepo
) : ViewModel() {
    private val deckId = savedStateHandle.get<Long>("deckId") ?: -1L
    private val dOptionId = savedStateHandle.get<Long>("optionId") ?: 1L
    private val selectedDeckOptionId = MutableStateFlow(dOptionId)

    val optionUsages = optionRepo.getDeckOptionUsage()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val optionData = combine(
        optionUsages, selectedDeckOptionId
    ) { list, id ->
        list.find { it.option.id == id }?.option ?: defaultDeckOption
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Lazily, defaultDeckOption)
    val isLoading = MutableStateFlow<Boolean>(false)

    val alertCannotDelete = MutableStateFlow(false)
    val saveOptionSuccess = MutableStateFlow(false)
    val showCheckConfirmSaveAction = MutableStateFlow(false)


    fun onAction(action: DeckOptionUiAction) {
        when (action) {
            is DeckOptionUiAction.Save -> {
                save(
                    newPerDay = action.newPerDay,
                    revPerDay = action.revPerDay,
                    autoPlayAudio = action.autoPlayAudio,
                    autoShowAnswer = action.autoShowAnswer,
                    fsrsParams = action.fsrsParams
                )
            }

            is DeckOptionUiAction.ChangeSelectedOption -> {
                changeSelectedOption(action.optionId)
                selectedDeckOptionId.value = action.optionId
            }

            DeckOptionUiAction.ClonePreset -> {
                clonePreset()
            }

            DeckOptionUiAction.DeletePreset -> {
                deletePreset()
            }

            is DeckOptionUiAction.NewPreset -> {
                newPreset(action.name)
            }

            is DeckOptionUiAction.RenamePreset -> {
                viewModelScope.launch {
                    optionRepo.update(optionData.value.id, action.name)
                }
            }

            DeckOptionUiAction.ConfirmAlertCannotDelete -> {
                alertCannotDelete.value = false
            }

            DeckOptionUiAction.ConfirmSaveSuccess -> {
                saveOptionSuccess.value = false
            }

            is DeckOptionUiAction.CheckConfirmSaveAction -> {
                checkConfirmSaveAction(
                    newPerDay = action.newPerDay,
                    revPerDay = action.revPerDay,
                    autoPlayAudio = action.autoPlayAudio,
                    autoShowAnswer = action.autoShowAnswer,
                    fsrsParams = action.fsrsParams
                )
            }

            is DeckOptionUiAction.CloneToNewPreset -> {
                cloneToNewPreset(
                    newPerDay = action.newPerDay,
                    revPerDay = action.revPerDay,
                    autoPlayAudio = action.autoPlayAudio,
                    autoShowAnswer = action.autoShowAnswer,
                    fsrsParams = action.fsrsParams
                )
            }

            DeckOptionUiAction.DismissCheckConfirmSaveAction -> {
                showCheckConfirmSaveAction.value = false
            }
        }
    }

    private fun checkConfirmSaveAction(
        newPerDay: Long,
        revPerDay: Long,
        autoPlayAudio: Boolean,
        autoShowAnswer: Long,
        fsrsParams: List<Double>
    ) {
        viewModelScope.launch {
            if (deckId == -1L) {
                save(newPerDay, revPerDay, autoPlayAudio, autoShowAnswer, fsrsParams)
                return@launch
            }
            showCheckConfirmSaveAction.value = true
        }
    }

    private fun changeSelectedOption(optionId: Long) {
        viewModelScope.launch {
            selectedDeckOptionId.value = optionId
            if (deckId != -1L) {
                deckRepo.update(id = deckId, optionId = optionId)
            }
        }
    }

    private fun newPreset(name: String) {
        viewModelScope.launch {
            val newId = optionRepo.insert(DeckOptionEntity.new(name))
            selectedDeckOptionId.value = newId
        }
    }

    private fun deletePreset() {
        viewModelScope.launch {
            if (optionData.value.id == defaultDeckOption.id) {
                alertCannotDelete.value = true
                return@launch
            }
            optionRepo.delete(optionData.value)
        }
    }

    private fun cloneToNewPreset(
        newPerDay: Long,
        revPerDay: Long,
        autoPlayAudio: Boolean,
        autoShowAnswer: Long,
        fsrsParams: List<Double>
    ) {
        viewModelScope.launch {
            val clone = DeckOptionEntity(
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                name = "Clone of " + optionData.value.name,
                newPerDay = newPerDay,
                revPerDay = revPerDay,
                autoAudio = autoPlayAudio,
                autoShowAnswer = autoShowAnswer,
                autoAnswer = false,
                fsrsParams = fsrsParams
            )

            val newOid = optionRepo.insert(clone)
            deckRepo.update(id = deckId, optionId = newOid)
        }
    }

    private fun clonePreset() {
        viewModelScope.launch {
            val clone = optionData.value.clone()
            val newId = optionRepo.insert(clone)
            selectedDeckOptionId.value = newId
        }
    }

    private fun save(
        newPerDay: Long,
        revPerDay: Long,
        autoPlayAudio: Boolean,
        autoShowAnswer: Long,
        fsrsParams: List<Double>
    ) {
        viewModelScope.launch {
            val a = optionData.value.copy(
                newPerDay = newPerDay,
                revPerDay = revPerDay,
                autoAudio = autoPlayAudio,
                autoShowAnswer = autoShowAnswer,
                fsrsParams = fsrsParams
            )
            optionRepo.update(a)
            saveOptionSuccess.value = true
        }
    }
}

sealed interface DeckOptionUiAction {
    data class Save(
        val newPerDay: Long,
        val revPerDay: Long,
        val autoPlayAudio: Boolean,
        val autoShowAnswer: Long,
        val fsrsParams: List<Double>
    ) : DeckOptionUiAction

    data class ChangeSelectedOption(val optionId: Long) : DeckOptionUiAction

    data class NewPreset(val name: String) : DeckOptionUiAction
    data class CloneToNewPreset(
        val newPerDay: Long,
        val revPerDay: Long,
        val autoPlayAudio: Boolean,
        val autoShowAnswer: Long,
        val fsrsParams: List<Double>
    ) : DeckOptionUiAction

    data object ClonePreset : DeckOptionUiAction

    data object DeletePreset : DeckOptionUiAction
    data class RenamePreset(val name: String) : DeckOptionUiAction

    data class CheckConfirmSaveAction(
        val newPerDay: Long,
        val revPerDay: Long,
        val autoPlayAudio: Boolean,
        val autoShowAnswer: Long,
        val fsrsParams: List<Double>
    ) : DeckOptionUiAction

    data object DismissCheckConfirmSaveAction : DeckOptionUiAction
    data object ConfirmAlertCannotDelete : DeckOptionUiAction
    data object ConfirmSaveSuccess : DeckOptionUiAction
}