package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.deck.SelectableDeck
import com.nmheir.kanicard.data.dto.note.SelectableNote
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.repository.FieldRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepo: INoteRepo,
    private val deckRepo: IDeckRepo,
    private val fieldRepo: FieldRepo
) : ViewModel() {

    val selectableDecks = deckRepo.getAllDecks()
        .mapNotNull {
            it?.map { deck ->
                SelectableDeck(id = deck.id, name = deck.name)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val noteTypes = noteRepo.getAllNoteTypes()
        .mapNotNull {
            it?.map {
                SelectableNote(id = it.id, name = it.name)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedNoteType = MutableStateFlow<SelectableNote?>(null)

    val fields = MutableStateFlow<List<FieldDefEntity>>(emptyList())

    val newTypeDialogUiState = MutableStateFlow<NewTypeDialogUiState>(NewTypeDialogUiState())

    init {
        viewModelScope.launch {
            selectedNoteType
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    loadFields(it.id)
                }
        }
    }

    fun onAction(action: NoteEditorUiAction) {
        when (action) {
            is NoteEditorUiAction.UpdateSelectedNoteType -> {
                selectedNoteType.value = action.noteType
            }

            is NoteEditorUiAction.AddNewField -> {
                val fields = action.fieldNames.mapIndexed { index, name ->
                    FieldDefEntity(
                        noteTypeId = selectedNoteType.value!!.id,
                        name = name,
                        ord = index,
                        createdTime = OffsetDateTime.now(),
                        modifiedTime = OffsetDateTime.now()
                    )
                }


            }

            is NoteEditorUiAction.CreateNewNoteType -> {
                viewModelScope.launch {
                    //Note should insert first, then field
                    val newTypeId = noteRepo.insert(
                        NoteTypeEntity(
                            name = action.typeName,
                            description = "",
                            createdTime = OffsetDateTime.now(),
                            modifiedTime = OffsetDateTime.now()
                        )
                    )
                    val fields = action.fieldNames.mapIndexed { index, name ->
                        FieldDefEntity(
                            noteTypeId = newTypeId,
                            name = name,
                            ord = index,
                            createdTime = OffsetDateTime.now(),
                            modifiedTime = OffsetDateTime.now()
                        )
                    }

                    fieldRepo.inserts(fields)
                }
            }
        }
    }

    private fun loadFields(noteTypeId: Long) {
        viewModelScope.launch {
            fields.value = noteRepo.getNoteTypeWithFieldDefs(noteTypeId)?.fieldDefs ?: emptyList()
        }
    }
}

sealed interface NoteEditorUiAction {
    data class UpdateSelectedNoteType(val noteType: SelectableNote?) : NoteEditorUiAction
    data class AddNewField(val fieldNames: List<String>) : NoteEditorUiAction
    data class CreateNewNoteType(val typeName: String, val fieldNames: List<String>) :
        NoteEditorUiAction
}

data class NewTypeDialogUiState(
    val newTypeField: List<String> = emptyList()
)