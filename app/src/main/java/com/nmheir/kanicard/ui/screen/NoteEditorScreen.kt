@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.IconButtonTooltip
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.core.presentation.utils.secondaryItemAlpha
import com.nmheir.kanicard.data.entities.note.FieldDefEntity
import com.nmheir.kanicard.ui.component.AlertDialog
import com.nmheir.kanicard.ui.component.ListOptionDialog
import com.nmheir.kanicard.ui.component.widget.PreferenceEntry
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.NewTypeDialogUiState
import com.nmheir.kanicard.ui.viewmodels.NoteEditorUiAction
import com.nmheir.kanicard.ui.viewmodels.NoteEditorViewModel
import com.nmheir.kanicard.utils.fakeFields

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    navController: NavHostController,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val deckId = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("deckId")

    val decks by viewModel.selectableDecks.collectAsStateWithLifecycle()
    val noteTypes by viewModel.noteTypes.collectAsStateWithLifecycle()
    val selectedNoteType by viewModel.selectedNoteType.collectAsStateWithLifecycle()
    val fields by viewModel.fields.collectAsStateWithLifecycle()

    val newTypeUiState by viewModel.newTypeDialogUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.action_add_cards)) },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_chevron_left), null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(painterResource(R.drawable.ic_visibility), null)
                    }
                    Box {
                        var showOption by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { showOption = true }
                        ) {
                            Icon(painterResource(R.drawable.ic_more_vert), null)
                        }
                        if (showOption) {
                            var showNewNoteTypeDialog by remember { mutableStateOf(false) }
                            DropdownOption(
                                modifier = Modifier.padding(end = 12.dp),
                                onDismiss = { showOption = false },
                                onClick = {
                                    when (it) {
                                        NoteEditorOption.NewNoteType -> {
                                            showNewNoteTypeDialog = true
                                        }

                                        NoteEditorOption.Clear -> {

                                        }
                                    }
                                }
                            )
                            if (showNewNoteTypeDialog) {
                                NewNoteTypeDialog(
                                    state = newTypeUiState,
                                    action = viewModel::onAction,
                                    onDismiss = { showNewNoteTypeDialog = false }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { pv ->
        LazyColumn(
            contentPadding = pv
        ) {
            item(
                key = "deck"
            ) {
                var showSelectDeckDialog by remember { mutableStateOf(false) }
                var selectedDeck by remember(decks) {
                    mutableStateOf(decks.firstOrNull { it.id == deckId })
                }
                OptionField(
                    title = selectedDeck?.name ?: "Select Deck",
                    leadingIcon = { Icon(painterResource(R.drawable.ic_folder), null) },
                    onClick = { showSelectDeckDialog = true }
                )
                if (showSelectDeckDialog) {
                    ListOptionDialog(
                        onDismiss = { showSelectDeckDialog = false },
                        values = decks,
                        selectedValue = selectedDeck,
                        onValueSelected = {
                            selectedDeck = it
                        },
                        valueText = { it?.name ?: "Con cac" }
                    )
                }
            }

            item(
                key = "type"
            ) {
                var showSelectNoteTypeDialog by remember { mutableStateOf(false) }
                OptionField(
                    title = selectedNoteType?.name ?: "Select Note Type",
                    leadingIcon = { Icon(painterResource(R.drawable.ic_folder), null) },
                    onClick = { showSelectNoteTypeDialog = true }
                )
                if (showSelectNoteTypeDialog) {
                    ListOptionDialog(
                        values = noteTypes,
                        selectedValue = selectedNoteType,
                        onValueSelected = {
                            viewModel.onAction(NoteEditorUiAction.UpdateSelectedNoteType(it))
                        },
                        valueText = { it?.name ?: "Con cac" },
                        onDismiss = { showSelectNoteTypeDialog = false }
                    )
                }
            }

            item(
                key = "template"
            ) {

            }

            items(
                items = fields,
                key = { it.id }
            ) { field ->
                if (fields.isEmpty()) {
                    Text(text = "No fields")
                }
                FieldEditElement(
                    field = field,
                    action = viewModel::onAction
                )
            }
        }
    }
}

@Composable
private fun OptionField(
    modifier: Modifier = Modifier,
    title: String,
    leadingIcon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            leadingIcon()
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(painterResource(R.drawable.ic_edit), null)
        }
    }
}

@Composable
private fun DropdownOption(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClick: (NoteEditorOption) -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        NoteEditorOption.entries.fastForEach { option ->
            DropdownMenuItem(
                text = { Text(text = option.title) },
                onClick = { onClick(option) }
            )
        }
    }
}

@Composable
private fun FieldEditElement(
    field: FieldDefEntity,
    action: (NoteEditorUiAction) -> Unit
) {
    var showAttachFileSheet by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.hozPadding()
        ) {
            Text(
                text = field.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButtonTooltip(
                iconRes = R.drawable.ic_attachment,
                contentDescription = "Attach File",
                shortCutDescription = "Attach File",
                onClick = { showAttachFileSheet = true }
            )
        }
        val (value, onValueChange) = remember { mutableStateOf("") }
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyLarge,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .hozPadding()
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(bottom = 4.dp)
                ) {
                    innerTextField()
                }
            }
        )
    }
    if (showAttachFileSheet) {
        AttachFileSheet(
            onDismiss = { showAttachFileSheet = false },
            onAudioClipSelection = { },
            onGallerySelection = { },
            onVideoClipSelection = { },
            onRecordAudioSelection = { }
        )
    }
}

@Composable
private fun AttachFileSheet(
    modifier: Modifier = Modifier,
    onAudioClipSelection: () -> Unit,
    onGallerySelection: () -> Unit,
    onVideoClipSelection: () -> Unit,
    onRecordAudioSelection: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AttachFileSheetOption.options.fastForEach { option ->
                TextPreferenceWidget(
                    title = option.title,
                    icon = option.iconRes,
                    onPreferenceClick = {
                        when (option) {
                            AttachFileSheetOption.AudioClip -> {

                            }
                            AttachFileSheetOption.Gallery -> {}
                            AttachFileSheetOption.RecordAudio -> {}
                            AttachFileSheetOption.VideoClip -> {}
                        }
                    }
                )
            }
        }
    }
}

private sealed class AttachFileSheetOption(
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object RecordAudio : AttachFileSheetOption("Record Audio", R.drawable.ic_record_voice_over)
    object Gallery : AttachFileSheetOption("Gallery", R.drawable.ic_image_fill)
    object AudioClip : AttachFileSheetOption("Add Audio Clip", R.drawable.ic_music_note)
    object VideoClip : AttachFileSheetOption("Add Video Clip", R.drawable.ic_video_library)

    companion object {
        val options = listOf(RecordAudio, Gallery, AudioClip, VideoClip)
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewNoteTypeDialog(
    modifier: Modifier = Modifier,
    state: NewTypeDialogUiState,
    action: (NoteEditorUiAction) -> Unit,
    onDismiss: () -> Unit
) {
    val (typeName, onTypeNameChange) = remember { mutableStateOf(state.typeName) }
    val list = remember { state.fields.toMutableStateList() }
    val shouldConfirmBeforeDismiss by remember {
        derivedStateOf {
            list != state.fields || typeName != state.typeName
        }
    }
    var showConfirmBeforeDismissDialog by remember { mutableStateOf(false) }
    AlertDialog(
        preventDismissRequest = true,
        enableButton = {
            typeName.isNotBlank() && list.any { it.isNotBlank() }
        },
        onDismiss = {
            if (shouldConfirmBeforeDismiss) {
                showConfirmBeforeDismissDialog = true
            } else {
                onDismiss()
            }
        },
        onConfirm = {
            action(NoteEditorUiAction.CreateNewNoteType(typeName, list))
            onDismiss()
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Type name: ")
                BasicTextField(
                    value = typeName,
                    onValueChange = onTypeNameChange,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    maxLines = 1,
                    decorationBox = { innerTextField ->
                        if (typeName.isEmpty()) {
                            Text(
                                text = "Enter your type name here",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .secondaryItemAlpha()
                        .weight(1f),
                )
            }

            list.forEachIndexed { index, fieldName ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // TODO: Implement drag
                    Icon(painterResource(R.drawable.ic_drag_indicator), null)
                    BasicTextField(
                        modifier = Modifier.weight(1f),
                        value = fieldName,
                        onValueChange = {
                            list[index] = it
                        },
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        decorationBox = { innerTextField ->
                            if (fieldName.isEmpty()) {
                                Text(
                                    text = "Enter your field name here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.secondaryItemAlpha()
                                )
                            }
                            innerTextField()
                        }
                    )
                    IconButton(
                        onClick = {
                            list.removeAt(index)
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_playlist_remove), null)
                    }
                }
            }

            OutlinedButton(
                onClick = { list.add("") }
            ) {
                Text(text = "Add field")
            }
        }
    }
    if (showConfirmBeforeDismissDialog) {
        AlertDialog(
            onDismiss = {
                showConfirmBeforeDismissDialog = false
                onDismiss()
            },
            onConfirm = {
                action(NoteEditorUiAction.SaveNoteToState(typeName, list))
                onDismiss()
            },
            title = {
                Text(text = "Do you want to keep this state?")
            }
        ) {
            Text(text = "You can continue edit this when you reopen.")
        }
    }
}

@Composable
private fun NewNoteTypeDialogElement(
    modifier: Modifier = Modifier,
    fieldName: String,
    onFieldNameChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Icon(painterResource(R.drawable.ic_drag_indicator), null)
        OutlinedTextField(
            value = fieldName,
            onValueChange = onFieldNameChange
        )
        IconButton(
            onClick = onRemove
        ) {
            Icon(painterResource(R.drawable.ic_playlist_remove), null)
        }
    }
}

private enum class NoteEditorOption(val title: String) {
    NewNoteType("New Note Type"), Clear("Clear")
}