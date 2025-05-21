@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.dto.note.NoteData
import com.nmheir.kanicard.ui.component.card.FlashCardLite
import com.nmheir.kanicard.ui.component.dialog.AlertDialog
import com.nmheir.kanicard.ui.component.dialog.TextFieldDialog
import com.nmheir.kanicard.ui.component.widget.PreferenceEntry
import com.nmheir.kanicard.ui.screen.note.CardSide
import com.nmheir.kanicard.ui.viewmodels.DeckDetailUiAction
import com.nmheir.kanicard.ui.viewmodels.DeckDetailViewModel

@Composable
fun DeckDetailScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeckDetailViewModel = hiltViewModel()
) {
    val deckData by viewModel.deckData.collectAsStateWithLifecycle()
    val notesData by viewModel.noteData.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = deckData.name) },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                },
                actions = {
                    Box {
                        var showMore by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMore = true }) {
                            Icon(painterResource(R.drawable.ic_more_vert), null)
                        }
                        if (showMore) {
                            DropdownMenu(
                                onDismissRequest = { showMore = false },
                                expanded = true,
                                offset = DpOffset(x = 0.dp, y = (-12).dp)
                            ) {
                                var showRenameDialog by remember { mutableStateOf(false) }
                                DeckDetailMenu.entries.fastForEach {
                                    DropdownMenuItem(
                                        text = { Text(text = it.title) },
                                        onClick = { showRenameDialog = true }
                                    )
                                }
                                if (showRenameDialog) {
                                    TextFieldDialog(
                                        onDismiss = { showRenameDialog = false },
                                        onDone = {
                                            viewModel.onAction(DeckDetailUiAction.RenameDeck(it))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Go to learn"
                )
            }
        }
    ) { pv ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = pv
        ) {
            item(
                key = "sample_note"
            ) {
                PreferenceEntry(
                    title = { Text(text = "Sample (From ${notesData.size} notes)") },
                    trailingContent = {
                        Text(
                            text = "See All",
                            textDecoration = TextDecoration.Underline
                        )
                    },
                    onClick = {
                        navController.navigate("${deckData.id}/browse_card")
                    }
                )
                SampleNoteSection(
                    data = notesData
                )
            }

            item(
                key = "deck_size"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.mediumSmall),
                    modifier = Modifier
                        .hozPadding()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_playing_cards),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp),
                    )
                    Text(
                        text = deckData.noteCount.toString() + if (deckData.noteCount > 1) " Cards" else " Card",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                HorizontalDivider(modifier = Modifier.hozPadding())
            }

            item(
                key = "deck_description"
            ) {
                val descriptionState = remember(deckData.description) {
                    TextFieldState(initialText = if (deckData.description.isEmpty()) "No description" else deckData.description)
                }

                var canEdit by remember { mutableStateOf(false) }
                var showConfirmDialog by remember { mutableStateOf(false) }
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismiss = { showConfirmDialog = false },
                        onConfirm = {
                            viewModel.onAction(DeckDetailUiAction.UpdateDescription(descriptionState.text.toString()))
                        },
                        preventDismissRequest = true
                    ) {
                        Text(text = "Do you want to save description ?")
                    }
                }
                PreferenceEntry(
                    title = { Text(text = "Description") },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                if (canEdit == false) {
                                    canEdit = true
                                } else {
                                    if (descriptionState.text.toString() != deckData.description) {
                                        showConfirmDialog = true
                                    } else {
                                        canEdit = false
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painterResource(if (!canEdit) R.drawable.ic_edit else R.drawable.ic_edit_fill),
                                null
                            )
                        }
                    }
                )
                BasicTextField(
                    state = descriptionState,
                    readOnly = !canEdit,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    decorator = { innerTextField ->
                        if (canEdit) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Black, MaterialTheme.shapes.small)
                                    .padding(12.dp)
                            ) {
                                innerTextField()
                            }
                        } else {
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .hozPadding()
                        .imePadding()
                )
            }
        }
    }
}

@Composable
private fun SampleNoteSection(
    data: List<NoteData>
) {
    val state = rememberLazyListState()
    BoxWithConstraints {
        val horizontalLazyGridItemWidthFactor =
            if (this.maxWidth * 0.475f >= 320.dp) 0.475f else 0.9f
        val horizontalLazyGridItemWidth = this.maxWidth * horizontalLazyGridItemWidthFactor

        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = MaterialTheme.padding.mediumSmall)
        ) {
            items(
                items = data,
                key = { it.id }
            ) {
                val flipController = rememberFlipController()
                Flippable(
                    modifier = Modifier
                        .width(horizontalLazyGridItemWidth),
                    frontSide = {
                        FlashCardLite(
                            text = it.qHtml,
                            side = CardSide.Front,
                            modifier = Modifier,
                            onClick = {
                                flipController.flipToBack()
                            }
                        )
                    },
                    backSide = {
                        FlashCardLite(
                            text = it.aHtml,
                            side = CardSide.Back,
                            modifier = Modifier,
                            onClick = {
                                flipController.flipToFront()
                            }
                        )
                    },
                    flipController = flipController
                )
            }
        }
    }
}

private enum class DeckDetailMenu(val title: String) {
    Rename("Rename")
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    var canEdit by remember { mutableStateOf(false) }
    val state = remember {
        TextFieldState("adolfjalkdfjlakdjflkasd")
    }



    Column {
        TextButton(
            onClick = { canEdit = !canEdit }
        ) {
            Text(text = "Edit")
        }

        BasicTextField(
            state = state,
            textStyle = MaterialTheme.typography.bodySmall,
            lineLimits = TextFieldLineLimits.MultiLine(1, 10),
            readOnly = !canEdit,
            decorator = { innerTextField ->
                if (canEdit) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .padding(4.dp)
                    ) {
                        innerTextField()
                    }
                } else {
                    innerTextField()
                }
            },
            modifier = Modifier.padding(4.dp)
        )
    }
}