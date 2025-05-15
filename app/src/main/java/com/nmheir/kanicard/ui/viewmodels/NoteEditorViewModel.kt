@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmheir.kanicard.data.dto.deck.SelectableDeck
import com.nmheir.kanicard.data.dto.note.SelectableNoteType
import com.nmheir.kanicard.data.entities.card.CardTemplateEntity
import com.nmheir.kanicard.data.entities.fsrs.FsrsCardEntity
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.data.entities.note.NoteEntity
import com.nmheir.kanicard.data.entities.note.NoteTypeEntity
import com.nmheir.kanicard.data.entities.note.buildFieldJson
import com.nmheir.kanicard.data.repository.FieldRepo
import com.nmheir.kanicard.domain.repository.ICardRepo
import com.nmheir.kanicard.domain.repository.IDeckRepo
import com.nmheir.kanicard.domain.repository.INoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val noteRepo: INoteRepo,
    private val cardRepo: ICardRepo,
    private val deckRepo: IDeckRepo,
    private val fieldRepo: FieldRepo
) : ViewModel() {
    private val deckId = savedStateHandle.getStateFlow<Long>("deckId", -1L)

    val isSaving = MutableStateFlow(false)
    val savingProgress = MutableStateFlow(0f)

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
    val newTypeDialogUiState = MutableStateFlow<NewTypeDialogUiState>(NewTypeDialogUiState())
    val mediaFileState = MutableStateFlow<Map<Long, Uri?>>(emptyMap())

    val fields = selectedNoteType
        .filterNotNull()
        .flatMapLatest {
            noteRepo.getNoteTypeWithFieldDefs(it.id).map {
                it?.fieldDefs ?: emptyList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val templates = selectedNoteType
        .filterNotNull()
        .flatMapLatest {
            noteRepo.getNoteTypeWithTemplates(it.id).map {
                if (it?.templates.isNullOrEmpty()) {
                    listOf(sampleTemplate)
                } else it.templates
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedDeck = combine(
        selectableDecks, deckId
    ) { decks, id ->
        decks.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val enableToSave = combine(
        selectedDeck, selectedNoteType, templates
    ) { deck, type, templates ->
        deck != null && type != null && templates.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val fieldValuesState = MutableStateFlow<List<FieldValue>>(emptyList())

    init {
        //Maybe add fieldJson in Note into combine
        combine(
            selectedNoteType,
            fields // nếu bạn thực sự cần 'fields'; nếu không, bỏ qua
        ) { type, _ ->
            type
        }
            .filterNotNull()                                 // chỉ khi type khác null
            .flatMapLatest { type ->
                fieldRepo.getFieldsByNoteTypeId(type.id)    // Flow<List<Field>>
                    .map { list ->
                        list?.map { field ->
                            FieldValue(id = field.id, fieldName = field.name)
                        }
                    }
            }
            .distinctUntilChanged()                         // tránh phát lại cùng giá trị
            .onEach { fieldValues ->
                fieldValuesState.value = fieldValues ?: emptyList()
                mediaFileState.update {
                    emptyMap()
                }
                fieldValuesState.value.map {
                    mediaFileState.update { state ->
                        state.plus(Pair(it.id, null))
                    }
                }
            }
            .launchIn(viewModelScope)
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
                updateFileState(action.fieldId, action.fileName, action.fileDir)
            }

            NoteEditorUiAction.SaveNote -> {
                saveNote()
            }
        }
    }

    private fun updateFileState(fieldId: Long, fileName: String, fileDir: Uri?) {
        viewModelScope.launch {
            fieldValuesState.update { currentList ->
                currentList.map {
                    if (it.id == fieldId) {
                        it.copy(value = TextFieldState(fileName))
                    } else {
                        it
                    }
                }
            }

            mediaFileState.update { currentState ->
                currentState + (fieldId to fileDir)
            }
            Timber.d(fieldValuesState.value.toString())
            Timber.d(mediaFileState.value.toString())
        }
    }

    private fun saveNote() {
        isSaving.value = true
        viewModelScope.launch {
            val medias = mediaFileState.value
            val fields = fieldValuesState.value
            val templates = templates.value
            val deckId = selectedDeck.value?.id ?: -1L
            fields.forEach {
                if (medias[it.id] != null) {
                    Timber.d(medias[it.id].toString())
                }
            }

            val fieldJson = buildFieldJson(fields)

            templates.forEachIndexed { index, template ->
                val note = NoteEntity(
                    deckId = deckId,
                    templateId = template.id,
                    fieldJson = fieldJson,
                    createdTime = OffsetDateTime.now(),
                    modifiedTime = OffsetDateTime.now()
                )
                val noteId = noteRepo.insert(note)
                cardRepo.insert(
                    FsrsCardEntity.createNew(deckId, noteId)
                )
                savingProgress.value = ((index + 1).toFloat() / templates.size).coerceAtMost(1f)
            }


            Toast.makeText(context, "Saved Note Success", Toast.LENGTH_SHORT).show()
            isSaving.value = false
        }
    }
}

sealed interface NoteEditorUiAction {
    data class UpdateSelectedDeck(val deck: SelectableDeck?) : NoteEditorUiAction
    data class UpdateSelectedNoteType(val noteType: SelectableNoteType?) : NoteEditorUiAction
    data class CreateNewNoteType(val typeName: String, val fieldNames: List<String>) :
        NoteEditorUiAction

    data class SaveNoteToState(val typeName: String, val fields: List<String>) : NoteEditorUiAction
    data class UpdateFileState(val fieldId: Long, val fileName: String, val fileDir: Uri?) :
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

private val sampleTemplate = CardTemplateEntity(
    id = 0L,
    noteTypeId = 0,
    name = "Template",
    qstFt = "",
    ansFt = ""
)

data class FieldValue(
    val id: Long,
    val fieldName: String,
    val value: TextFieldState = TextFieldState("")
)