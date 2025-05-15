@file:OptIn(ExperimentalFoundationApi::class)

package com.nmheir.kanicard.ui.viewmodels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.domain.repository.INoteRepo
import com.nmheir.kanicard.domain.repository.ITemplateRepo
import com.nmheir.kanicard.extensions.add
import com.nmheir.kanicard.extensions.addHeader
import com.nmheir.kanicard.extensions.addInNewLine
import com.nmheir.kanicard.extensions.bold
import com.nmheir.kanicard.extensions.italic
import com.nmheir.kanicard.extensions.md.MarkdownWithParametersParser
import com.nmheir.kanicard.extensions.strikeThrough
import com.nmheir.kanicard.extensions.tab
import com.nmheir.kanicard.extensions.unTab
import com.nmheir.kanicard.extensions.underline
import com.nmheir.kanicard.ui.screen.note.CardSide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.absoluteValue

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

    val isLoading = MutableStateFlow(false)

    val fields = noteTypeWithFields
        .map { it?.fieldDefs ?: emptyList() }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val templates = MutableStateFlow<List<TemplateState>>(emptyList())

    val type = noteTypeWithTemplates.map {
        it?.noteType
    }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        templateRepo.getTemplatesByNoteTypeId(noteTypeId)
            .map {
                if (it.isNullOrEmpty()) {
                    Timber.d("No template")
                    listOf(sampleTemplate)
                } else {
                    Timber.d("Has template")
                    it.map { template ->
                        template.toTemplateState()
                    }
                }
            }.distinctUntilChanged()
            .onEach {
                templates.value = it
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: NoteTemplateUiAction) {
        when (action) {
            is NoteTemplateUiAction.AddNewTemplate -> {
                templates.update {
                    val lastId = it.last().id
                    val tempId = if (lastId > 0L) {
                        lastId.inc() * -1L
                    } else lastId.dec()

                    it + TemplateState(
                        id = tempId,
                        name = "Card ${tempId.absoluteValue}",
                        qstState = TextFieldState(),
                        ansState = TextFieldState()
                    )
                }
            }

            is NoteTemplateUiAction.DeleteTemplate -> {
                val id = templates.value[action.index].id
                if (id < 0L) {
                    templates.update {
                        it.toMutableList().apply {
                            removeAt(action.index)
                        }
                    }
                } else {
                    viewModelScope.launch { templateRepo.delete(id) }
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
                    Constants.Editor.INSERT_FIELD -> contentState.edit {
                        append(" {{${action.value}}} ")
                    }

                    Constants.Editor.TEXT -> contentState.edit { add(action.value) }

                    else -> {}
                }
            }

            is NoteTemplateUiAction.Save -> {
                isLoading.value = true
                viewModelScope.launch {
                    val (newTemplates, oldTemplates) =
                        templates.value.partition { it.id < 0L }

                    // When you add a new template, its id will be written as a negative number to distinguish it.
                    val newTemplateEntity = newTemplates.map {
                        CardTemplateEntity(
                            noteTypeId = noteTypeId,
                            name = it.name,
                            qstFt = it.qstState.text.toString(),
                            ansFt = it.ansState.text.toString()
                        )
                    }

                    // I think it would be more optimal to check which components changed but I find it too complicated
                    val oldTemplateEntity = oldTemplates.map {
                        CardTemplateEntity(
                            id = it.id,
                            noteTypeId = noteTypeId,
                            name = it.name,
                            qstFt = it.qstState.text.toString(),
                            ansFt = it.ansState.text.toString()
                        )
                    }
                    templateRepo.inserts(newTemplateEntity)
                    templateRepo.inserts(oldTemplateEntity)
                    isLoading.value = false
                }
            }

            is NoteTemplateUiAction.RenameNoteType -> {
                viewModelScope.launch {
                    noteRepo.update(type.value!!.copy(name = action.name))
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

    data class RenameNoteType(val name: String) : NoteTemplateUiAction

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

data class TemplatePreview(
    val id: Long,
    val name: String,
    val qstHtml: String,
    val ansHtml: String
)

fun CardTemplateEntity.toTemplateState(): TemplateState {
    return TemplateState(
        id = this.id,
        name = this.name,
        qstState = TextFieldState(initialText = this.qstFt),
        ansState = TextFieldState(initialText = this.ansFt)
    )
}

fun TemplateState.toTemplatePreview(): TemplatePreview {
    return TemplatePreview(
        id = this.id,
        name = this.name,
        qstHtml = MarkdownWithParametersParser.parseToHtml(
            this.qstState.text.toString(),
            emptyMap()
        ),
        ansHtml = MarkdownWithParametersParser.parseToHtml(
            this.ansState.text.toString(),
            emptyMap()
        )
    )
}