@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.AlertDialog
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.MarkdownEditorRow
import com.nmheir.kanicard.ui.component.TextFieldDialog
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.NoteTemplateUiAction
import com.nmheir.kanicard.ui.viewmodels.NoteTemplateViewModel
import com.nmheir.kanicard.ui.viewmodels.TemplateState
import com.nmheir.kanicard.ui.viewmodels.toTemplatePreview
import com.nmheir.kanicard.ui.viewmodels.toTemplateState
import com.nmheir.kanicard.utils.fakeTemplatePreviews
import kotlinx.coroutines.launch

@Composable
fun NoteTemplateScreen(
    navController: NavHostController,
    viewModel: NoteTemplateViewModel = hiltViewModel()
) {
    val type by viewModel.type.collectAsStateWithLifecycle()
    if (type == null) return
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val fields by viewModel.fields.collectAsStateWithLifecycle()

    val snapshotTemplates = remember { templates.toMutableStateList() }

    val pagerState = rememberPagerState(initialPage = 0) {
        templates.size
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isPreview by remember { mutableStateOf(false) }

    BackHandler(isPreview) {
        if (isPreview) {
            focusManager.clearFocus()
            isPreview = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Card Types",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = type?.name ?: "No type",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    var showConfirmDialog by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                            if (snapshotTemplates != templates) {
                                showConfirmDialog = true
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                    if (showConfirmDialog) {
                        AlertDialog(
                            onDismiss = {
                                showConfirmDialog = false
                                navController.navigateUp()
                            },
                            onConfirm = {
                                // TODO: Thêm hoặc cập nhật template vào trong database
                            }
                        ) {
                            Text(text = "Do you want to keep this changes ?")
                        }
                    }
                },
                actions = {
                    IconButton(
                        enabled = snapshotTemplates != templates,
                        onClick = {
                            // TODO: Save template to database
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_check), null)

                    }
                    IconButton(
                        onClick = {
                            isPreview = !isPreview
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_visibility_fill), null)
                    }
                    Box {
                        var showOption by remember { mutableStateOf(false) }
                        IconButton(onClick = { showOption = true }) {
                            Icon(painterResource(R.drawable.ic_more_vert), null)
                        }
                        if (showOption) {
                            var showRenameDialog by remember { mutableStateOf(false) }
                            var showInsertFieldDialog by remember { mutableStateOf(false) }
                            DropdownOption(
                                onDismiss = { showOption = false },
                                onClick = {
                                    when (it) {
                                        NoteTemplateOption.Add -> {
                                            // TODO: Tìm cách bỏ id khi thêm template vào database
                                            viewModel.onAction(NoteTemplateUiAction.AddNewTemplate)
                                            scope.launch {
                                                //Scroll to last template
                                                pagerState.animateScrollToPage(templates.size - 1)
                                            }
                                            showOption = false
                                        }

                                        NoteTemplateOption.Rename -> {
                                            showRenameDialog = true
                                        }

                                        NoteTemplateOption.Delete -> {
                                            if (templates.size > 1) {
                                                viewModel.onAction(
                                                    NoteTemplateUiAction.DeleteTemplate(
                                                        pagerState.currentPage
                                                    )
                                                )
                                                scope.launch {
                                                    pagerState.animateScrollToPage(templates.size)
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Note type must have at least one template",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            showOption = false
                                        }

                                        NoteTemplateOption.InsertField -> {
                                            showInsertFieldDialog = true
                                        }

                                        NoteTemplateOption.CopyAsMarkdown -> {
                                            Toast.makeText(
                                                context,
                                                "Not implement",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                            if (showRenameDialog) {
                                TextFieldDialog(
                                    onDismiss = { showRenameDialog = false },
                                    initialTextFieldValue = TextFieldValue(
                                        text = type?.name ?: "",
                                        selection = TextRange(type?.name?.length ?: 0)
                                    ),
                                    placeholder = { Text(text = "Rename type") },
                                    onDone = {},
                                    title = {
                                        Text(text = "Rename type")
                                    }
                                )
                            }
                            if (showInsertFieldDialog) {
                                DefaultDialog(
                                    onDismiss = { showInsertFieldDialog = false },
                                    buttons = {
                                        TextButton(onClick = { showInsertFieldDialog = false }) {
                                            Text(text = stringResource(R.string.cancel))
                                        }
                                    }
                                ) {
                                    fields.fastForEach {
                                        TextPreferenceWidget(
                                            title = it.name,
                                            onPreferenceClick = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier.padding(pv)
        ) {
            ScrollableTabRow(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTabIndex = pagerState.currentPage.coerceIn(0, templates.size - 1),
                edgePadding = 0.dp,
                modifier = Modifier
            ) {
                templates.forEachIndexed { index, template ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = template.name)
                        }
                    )
                }
            }

            NoteTemplatePager(
                pagerState = pagerState,
                templates = templates,
                action = viewModel::onAction
            )

            AnimatedVisibility(
                visible = isPreview
            ) {
                val templatePreview = templates[pagerState.currentPage].toTemplatePreview()
                ModalBottomSheet(
                    dragHandle = null,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    onDismissRequest = { isPreview = false }
                ) {
                    PreviewContent(
                        template = templatePreview
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteTemplatePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    templates: List<TemplateState>,
    action: (NoteTemplateUiAction) -> Unit
) {
    HorizontalPager(
        modifier = modifier
            .fillMaxSize(),
        beyondViewportPageCount = templates.size,
        state = pagerState,
//        key = {
////            templates[it].id.coerceIn(0L, templates.size.toLong())
//        }
    ) { page ->
        NoteTemplateContent(
            template = templates[page],
            action = { side, key, value ->
                action(NoteTemplateUiAction.Edit(key, value, page, side))
            }
        )
    }
}


@Composable
private fun NoteTemplateContent(
    modifier: Modifier = Modifier,
    template: TemplateState,
    action: (CardSide, String, String) -> Unit
) {
    val (selectedCardSide, onSelectedCardSideChange) = remember { mutableStateOf<CardSide>(CardSide.Front) }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        bottomBar = {
            Column {
                MarkdownEditorRow(
                    canRedo = false,
                    canUndo = false,
                    onEdit = { key ->
                        action(selectedCardSide, key, "")
                    },
                    onListButtonClick = {}
                )
                NoteTemplateBottomBar(
                    selectedCardSide = selectedCardSide,
                    onSelectedCardSideChange = onSelectedCardSideChange,
                )
            }
        }
    ) { pv ->
        when (selectedCardSide) {
            CardSide.Front -> {
                NoteTemplateTextField(
                    modifier = Modifier
                        .padding(pv)
                        .fillMaxSize(),
                    state = template.qstState,
                    side = CardSide.Front
                )
            }

            CardSide.Back -> {
                NoteTemplateTextField(
                    modifier = Modifier
                        .padding(pv)
                        .fillMaxSize(),
                    state = template.ansState,
                    side = CardSide.Back
                )
            }
        }
    }
}

@Composable
private fun NoteTemplateTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    side: CardSide
) {
    BasicTextField(
        modifier = modifier
            .padding(start = 12.dp)
            .fillMaxSize(),
        state = state,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.None
        ),
        decorator = { innerTextField ->
            if (state.text.isEmpty()) {
                Text(
                    text = stringResource(
                        when (side) {
                            CardSide.Front -> R.string.label_question
                            CardSide.Back -> R.string.label_answer
                        }
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            innerTextField()
        }
    )
}

@Composable
private fun NoteTemplateBottomBar(
    modifier: Modifier = Modifier,
    selectedCardSide: CardSide,
    onSelectedCardSideChange: (CardSide) -> Unit
) {
    NavigationBar(
        modifier = modifier
            .imePadding()
            .fillMaxWidth()
    ) {
        CardSide.entries.fastForEach { cardSide ->
            NavigationBarItem(
                selected = selectedCardSide == cardSide,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = { onSelectedCardSideChange(cardSide) },
                icon = {
                    Icon(painterResource(cardSide.icon), null)
                },
                label = {
                    Text(stringResource(cardSide.title))
                }
            )
        }
    }
}

@Composable
private fun DropdownOption(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClick: (NoteTemplateOption) -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = true,
        onDismissRequest = onDismiss,
    ) {
        NoteTemplateOption.entries.fastForEach {
            DropdownMenuItem(
                text = { Text(text = stringResource(it.title)) },
                onClick = { onClick(it) }
            )
        }
    }
}

enum class CardSide(val title: Int, val icon: Int) {
    Front(R.string.label_front, R.drawable.ic_card_question),
    Back(R.string.label_back, R.drawable.ic_card_answer)
}

private enum class NoteTemplateOption(
    @StringRes val title: Int
) {
    Add(R.string.action_add),
    Rename(R.string.action_rename),
    Delete(R.string.action_delete),
    InsertField(R.string.action_insert_field),
    CopyAsMarkdown(R.string.action_copy_as_markdown)
}