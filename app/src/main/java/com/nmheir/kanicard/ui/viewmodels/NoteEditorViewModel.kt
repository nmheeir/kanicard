package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.deck.SelectableDeck
import com.nmheir.kanicard.data.dto.note.NoteEditDto
import com.nmheir.kanicard.data.dto.note.SelectableNoteType
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.repository.FieldRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo,
    private val deckRepo: IDeckRepo,
    private val fieldRepo: FieldRepo
) : ViewModel() {
    private val deckId = savedStateHandle.getStateFlow<Long>("deckId", -1L)

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
                SelectableNoteType(id = it.id, name = it.name)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedNoteType = MutableStateFlow<SelectableNoteType?>(null)
    val fields = MutableStateFlow<List<FieldDefEntity>>(emptyList())
    val newTypeDialogUiState = MutableStateFlow<NewTypeDialogUiState>(NewTypeDialogUiState())
    val mediaFileState = MutableStateFlow<Map<Long, Pair<String, Uri>>>(emptyMap())

    val selectedDeck = combine(
        selectableDecks, deckId
    ) { decks, id ->
        decks.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val noteEditDto = combine(
        selectedNoteType, fields, mediaFileState
    ) { type, fields, mediaFiles ->
        val fieldMap = fields.associate { field ->
            val value = mediaFiles[field.id]?.first ?: ""
            field.name to value
        }

        NoteEditDto(
            deckId = deckId.value,
            typeId = type?.id ?: 0,
            templateId = 0,
            field = fieldMap
        )

    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

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
            is NoteEditorUiAction.UpdateSelectedDeck -> {
                savedStateHandle["deckId"] = action.deck?.id ?: 0L
            }

            is NoteEditorUiAction.UpdateSelectedNoteType -> {
                selectedNoteType.value = action.noteType
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
                Toast.makeText(context, "Created", Toast.LENGTH_SHORT).show()
            }

            is NoteEditorUiAction.SaveNoteToState -> {
                viewModelScope.launch {
                    newTypeDialogUiState.update {
                        it.copy(
                            fields = action.fields,
                            typeName = action.typeName
                        )
                    }
                }
            }

            is NoteEditorUiAction.UpdateFileState -> {
                updateFileState(action.fieldId, action.name, action.fileDir)
            }

            NoteEditorUiAction.SaveNote -> {

            }
        }
    }

    private fun loadFields(noteTypeId: Long) {
        viewModelScope.launch {
            fields.value = noteRepo.getNoteTypeWithFieldDefs(noteTypeId)?.fieldDefs ?: emptyList()
        }
    }

    private fun updateFileState(fieldId: Long, name: String, fileDir: Uri) {
        viewModelScope.launch {
            mediaFileState.update { currentState ->
                currentState + (fieldId to (name to fileDir))
                /*currentState.toMutableMap().apply {
                    this[fieldId] = Pair(name, fileDir)
                }*/
            }
        }
    }
}

sealed interface NoteEditorUiAction {
    data class UpdateSelectedDeck(val deck: SelectableDeck?) : NoteEditorUiAction
    data class UpdateSelectedNoteType(val noteType: SelectableNoteType?) : NoteEditorUiAction
    data class CreateNewNoteType(val typeName: String, val fieldNames: List<String>) :
        NoteEditorUiAction

    data class SaveNoteToState(val typeName: String, val fields: List<String>) : NoteEditorUiAction
    data class UpdateFileState(val fieldId: Long, val name: String, val fileDir: Uri) :
        NoteEditorUiAction

    data object SaveNote : NoteEditorUiAction
}

data class NewTypeDialogUiState(
    val fields: List<String> = listOf<String>(""),
    val typeName: String = ""
)

sealed interface FileDataEvent {

    data class ImportImages(
        val context: Context,
        val uriList: List<Uri>
    ) : FileDataEvent

    data class ImportVideo(
        val context: Context,
        val uri: Uri
    ) : FileDataEvent
}