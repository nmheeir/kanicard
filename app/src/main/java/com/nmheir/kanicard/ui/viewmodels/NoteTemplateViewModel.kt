@file:OptIn(ExperimentalFoundationApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.TextField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import com.nmheir.kanicard.extensions.addHeader
import com.nmheir.kanicard.extensions.addInNewLine
import com.nmheir.kanicard.extensions.bold
import com.nmheir.kanicard.extensions.italic
import com.nmheir.kanicard.extensions.strikeThrough
import com.nmheir.kanicard.extensions.tab
import com.nmheir.kanicard.extensions.unTab
import com.nmheir.kanicard.extensions.underline
import com.nmheir.kanicard.ui.screen.note.CardSide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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

    val templates = MutableStateFlow<List<TemplateState>>(listOf(sampleTemplate))

    val type = noteTypeWithTemplates.map {
        it?.noteType
    }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        templates.value = noteTypeWithTemplates
            .map {
                if (it?.templates.isNullOrEmpty()) {
                    listOf(sampleTemplate)
                } else {
                    it.templates.map { template ->
                        template.toTemplateState()
                    }
                }
            }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, listOf(sampleTemplate)).value
    }

    fun onAction(action: NoteTemplateUiAction) {
        when (action) {
            is NoteTemplateUiAction.AddNewTemplate -> {
                templates.update {
                    it + TemplateState(
                        id = it.last().id.inc(),
                        name = "Card ${it.last().id.inc()}",
                        qstState = TextFieldState(),
                        ansState = TextFieldState()
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

            is NoteTemplateUiAction.Edit -> {
                val contentState = when (action.side) {
                    CardSide.Front -> templates.value[action.index].qstState
                    CardSide.Back -> templates.value[action.index].ansState
                }

                when (action.key) {
                    Constants.Editor.UNDO -> contentState.undoState.undo()
                    Constants.Editor.REDO -> contentState.undoState.redo()
                    Constants.Editor.H1 -> contentState.edit { addHeader(1) }
                    Constants.Editor.H2 -> contentState.edit { addHeader(2) }
                    Constants.Editor.H3 -> contentState.edit { addHeader(3) }
                    Constants.Editor.H4 -> contentState.edit { addHeader(4) }
                    Constants.Editor.H5 -> contentState.edit { addHeader(5) }
                    Constants.Editor.H6 -> contentState.edit { addHeader(6) }
                    Constants.Editor.BOLD -> contentState.edit { bold() }
                    Constants.Editor.ITALIC -> contentState.edit { italic() }
                    Constants.Editor.UNDERLINE -> contentState.edit { underline() }
                    Constants.Editor.STRIKETHROUGH -> contentState.edit { strikeThrough() }
                    Constants.Editor.TAB -> contentState.edit { tab() }
                    Constants.Editor.UN_TAB -> contentState.edit { unTab() }
                    Constants.Editor.LIST -> contentState.edit { addInNewLine(action.value) }

                    else -> {}
                }
            }

            NoteTemplateUiAction.Save -> {
                // TODO: Save template to database
                viewModelScope.launch {
                    Timber.d("Save %s", templates.value.toString())
                }
            }
        }
    }
}

sealed interface NoteTemplateUiAction {
    data object AddNewTemplate : NoteTemplateUiAction
    data class DeleteTemplate(val index: Int) : NoteTemplateUiAction
    data class Edit(val key: String, val value: String = "", val index: Int, val side: CardSide) :
        NoteTemplateUiAction

    data object Save : NoteTemplateUiAction
}

private val sampleTemplate = TemplateState(
    id = 0L,
    name = "Sample Template",
    qstState = TextFieldState(),
    ansState = TextFieldState()
)

data class TemplateState(
    val id: Long,
    val name: String,
    val qstState: TextFieldState,
    val ansState: TextFieldState
)

fun CardTemplateEntity.toTemplateState(): TemplateState {
    return TemplateState(
        id = this.id,
        name = this.name,
        qstState = TextFieldState(initialText = this.qstFt),
        ansState = TextFieldState(initialText = this.ansFt)
    )
}
