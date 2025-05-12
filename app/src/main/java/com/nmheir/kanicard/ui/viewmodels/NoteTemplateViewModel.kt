package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NoteTemplateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo,
    private val templateRepo: ITemplateRepo
) : ViewModel() {
    val noteTypeId = savedStateHandle.get<Long>("type")!!

    private val noteTypeWithTemplates = noteRepo.getNoteTypeWithTemplates(noteTypeId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val noteTypeWithFields = noteRepo.getNoteTypeWithFieldDefs(noteTypeId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val fields = noteTypeWithFields
        .map { it?.fieldDefs ?: emptyList() }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val templates = MutableStateFlow<List<CardTemplateEntity>>(listOf(sampleTemplate))

    val type = noteTypeWithTemplates.map {
        it?.noteType
    }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        templates.value = noteTypeWithTemplates
            .map {
                if (it?.templates.isNullOrEmpty()) {
                    listOf(sampleTemplate)
                } else it.templates
            }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, listOf(sampleTemplate)).value
    }

    fun onAction(action: NoteTemplateUiAction) {
        when (action) {
            is NoteTemplateUiAction.AddNewTemplate -> {
                templates.update {
                    it + sampleTemplate.copy(
                        id = it.last().id + 1L,
                        noteTypeId = noteTypeId,
                        name = "Card ${it.last().id + 1}"
                    )
                }
            }

            is NoteTemplateUiAction.DeleteTemplate -> {
                templates.update {
                    it.toMutableList().apply {
                        removeAt(action.index)
                    }
                }
            }
        }
    }
}

sealed interface NoteTemplateUiAction {
    data object AddNewTemplate : NoteTemplateUiAction
    data class DeleteTemplate(val index: Int) : NoteTemplateUiAction
}

val sampleTemplate = CardTemplateEntity(
    noteTypeId = 0L,
    name = "Sample Template",
    qstFt = "",
    ansFt = ""
)