package com.nmheir.kanicard.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.utils.dashedBorder
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.core.presentation.utils.secondaryItemAlpha
import com.nmheir.kanicard.ui.component.AlertDialog
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.ListOptionDialog
import com.nmheir.kanicard.ui.viewmodels.NewTypeDialogUiState
import com.nmheir.kanicard.ui.viewmodels.NoteEditorUiAction
import com.nmheir.kanicard.ui.viewmodels.NoteEditorViewModel

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
            ) {
                if (fields.isEmpty()) {
                    Text(text = "No fields")
                }
                Text(text = it.name)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewNoteTypeDialog(
    modifier: Modifier = Modifier,
    state: NewTypeDialogUiState,
    action: (NoteEditorUiAction) -> Unit,
    onDismiss: () -> Unit
) {
    val list = remember { mutableStateListOf<String>("") }
    val shouldConfirmBeforeDismiss by remember {
        derivedStateOf {
            list.any { it.isNotBlank() }
        }
    }
    var showConfirmBeforeDismissDialog by remember { mutableStateOf(false) }
    val (typeName, onTypeNameChange) = remember { mutableStateOf("") }
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
            onConfirm = {},
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