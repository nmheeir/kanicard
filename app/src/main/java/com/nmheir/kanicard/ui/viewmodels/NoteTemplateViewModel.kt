package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import com.nmheir.kanicard.utils.fakeTemplates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    val templates = noteTypeWithTemplates
        .map { it?.templates ?: emptyList() }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val type = noteTypeWithTemplates.map {
        it?.noteType
    }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onAction(action: NoteTemplateUiAction) {
        when (action) {
            is NoteTemplateUiAction.AddNewTemplate -> {
                viewModelScope.launch {
                    templateRepo.insert(
                        CardTemplateEntity(
                            noteTypeId = noteTypeId,
                            name = action.name,
                            qstFt = "",
                            ansFt = ""
                        )
                    )
                }
            }
        }
    }
}

sealed interface NoteTemplateUiAction {
    data class AddNewTemplate(val name: String) : NoteTemplateUiAction
}