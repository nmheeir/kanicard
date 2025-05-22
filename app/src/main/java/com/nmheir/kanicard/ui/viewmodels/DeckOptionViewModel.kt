@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.option.defaultDeckOption
import com.nmheir.kanicard.domain.repository.IDeckOptionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckOptionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val optionRepo: IDeckOptionRepo
) : ViewModel() {
    private val dOptionId = savedStateHandle.get<Long>("optionId") ?: 1L

    val selectedDeckOption = MutableStateFlow(dOptionId)

    val optionData = selectedDeckOption
        .flatMapLatest {
            optionRepo.getDeckOption(it)
        }.stateIn(viewModelScope, SharingStarted.Lazily, defaultDeckOption)

    val optionUsages = optionRepo.getDeckOptionUsage()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isLoading = MutableStateFlow(false)


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

            is DeckOptionUiAction.UpdateSelectedOption -> {
                selectedDeckOption.value = action.optionId
            }
        }
    }

    private fun save(
        newPerDay: Long,
        revPerDay: Long,
        autoPlayAudio: Boolean,
        autoShowAnswer: Long,
        fsrsParams: List<Double>
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val a = optionData.value.copy(
                newPerDay = newPerDay,
                revPerDay = revPerDay,
                autoAudio = autoPlayAudio,
                autoShowAnswer = autoShowAnswer,
                fsrsParams = fsrsParams
            )
            optionRepo.update(a)
            isLoading.value = false
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

    data class UpdateSelectedOption(val optionId: Long) : DeckOptionUiAction
}