package com.nmheir.kanicard.ui.screen

//import com.nmheir.kanicard.ui.component.DeckItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.core.presentation.utils.secondaryItemAlpha
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.data.entities.deck.CollectionEntity
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.AlertDialog
import com.nmheir.kanicard.ui.component.DeckItem
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.HideOnScrollFAB
import com.nmheir.kanicard.ui.component.ListDialog
import com.nmheir.kanicard.ui.component.TextFieldDialog
import com.nmheir.kanicard.ui.viewmodels.HomeUiAction
import com.nmheir.kanicard.ui.viewmodels.HomeUiEvent
import com.nmheir.kanicard.ui.viewmodels.HomeViewModel
import com.nmheir.kanicard.utils.ObserveAsEvents
import com.nmheir.kanicard.utils.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()

    val lazyListState = rememberLazyListState()

    val deckWidgetData by viewModel.deckWidgetData.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()

    var showOption by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        },
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.padding.small)
    ) {

        HomeContent(
            state = lazyListState,
            data = deckWidgetData,
            navController = navController,
            action = viewModel::onAction
        )

        HideOnScrollFAB(
            visible = true,
            lazyListState = lazyListState,
            icon = R.drawable.ic_add,
            onClick = { showOption = !showOption }
        )

        AnimatedVisibility(
            visible = showOption && lazyListState.isScrollingUp(),
            modifier = Modifier
                .padding(end = 80.dp) // >= 72 (56 + 16)
                .padding(LocalAwareWindowInset.current.asPaddingValues())
                .align(Alignment.BottomCenter)
        ) {
            OptionDialog(
                navController = navController,
                collections = collections,
                action = viewModel::onAction
            )
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMsg by remember { mutableStateOf<Int>(0) }
    ObserveAsEvents(viewModel.channel) {
        when (it) {
            is HomeUiEvent.Failure -> {
                showDialog = true
                dialogMsg = it.msgRes
            }

            is HomeUiEvent.Success -> {

            }
        }
    }

    if (showDialog) {
        DefaultDialog(
            onDismiss = { showDialog = false }
        ) {
            Text(text = stringResource(dialogMsg))
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    data: List<DeckWidgetData>,
    navController: NavHostController,
    action: (HomeUiAction) -> Unit
) {
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        contentPadding = LocalAwareWindowInset.current.asPaddingValues(),
        modifier = modifier
    ) {
        items(
            items = data,
            key = { it.deckId }
        ) { deckWidgetData ->
            Box {
                var showOptionDialog by rememberSaveable { mutableStateOf(false) }
                var showEditDialog by remember { mutableStateOf(false) }
                DeckItem(
                    deck = deckWidgetData,
                    onLearn = { navController.navigate("learn/${deckWidgetData.deckId}") },
                    onEdit = { showEditDialog = true },
                    onView = { navController.navigate("deck/1") },
                    onOption = { showOptionDialog = true }
                )
                if (showOptionDialog) {
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    DeckOptionDialog(
                        onDismiss = { showOptionDialog = false },
                        deckName = deckWidgetData.name,
                        onClick = {
                            when (it) {
                                DeckOptions.Edit -> {}
                                DeckOptions.Config -> {}
                                DeckOptions.Delete -> {
                                    showDeleteDialog = true
                                }
                            }
                        }
                    )
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismiss = { showDeleteDialog = false },
                            onConfirm = {},
                            icon = { Icon(painterResource(R.drawable.ic_delete), null) }
                        ) {
                            Text(
                                text = stringResource(R.string.alert_delete_deck),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                if (showEditDialog) {
                    TextFieldDialog(
                        onDismiss = { showEditDialog = false },
                        title = { Text(text = stringResource(R.string.label_edit_deck_name)) },
                        initialTextFieldValue = TextFieldValue(
                            text = deckWidgetData.name,
                            selection = TextRange(deckWidgetData.name.length)
                        ),
                        onDone = {
//                            action(HomeUiAction.UpdateDeckName(deckWidgetData.deckId, it))
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun DeckOptionDialog(
    deckName: String,
    onDismiss: () -> Unit,
    onClick: (DeckOptions) -> Unit
) {
    ListDialog(
        onDismiss = onDismiss
    ) {
        item {
            Text(
                text = deckName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .hozPadding()
            )
        }
        items(
            items = DeckOptions.entries
        ) { option ->
            Text(
                text = option.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(option) }
                    .padding(
                        horizontal = MaterialTheme.padding.medium,
                        vertical = MaterialTheme.padding.mediumSmall
                    )
            )
        }
    }
}

@Composable
private fun MyLatestReview(modifier: Modifier = Modifier) {
    // TODO: Need to do
}

@Composable
private fun OptionDialog(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    collections: List<CollectionEntity>,
    action: (HomeUiAction) -> Unit
) {
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column {
            homeAddOptions.fastForEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            when (item.title) {
                                R.string.action_add_cards -> {
                                    navController.navigate("add_new_card")
                                }

                                R.string.action_add_deck -> {
                                    showCreateDeckDialog = true
                                }
                            }
                        }
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraSmall)
                        .padding(
                            horizontal = MaterialTheme.padding.medium,
                            vertical = MaterialTheme.padding.medium
                        )
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Gap(MaterialTheme.padding.small)
                    Text(
                        text = stringResource(item.title),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            if (showCreateDeckDialog) {
                CreateDeckDialog(
                    onDismiss = { showCreateDeckDialog = false },
                    action = action,
                    collections = collections
                )
            }
        }
    }
}

@Composable
private fun CreateDeckDialog(
    onDismiss: () -> Unit,
    collections: List<CollectionEntity>,
    action: (HomeUiAction) -> Unit
) {
    DefaultDialog(
        onDismiss = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
        ) {
            var selectedCollection by remember { mutableStateOf<CollectionEntity?>(null) }
            val (name, onNameChange) = remember { mutableStateOf("") }
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text(text = stringResource(R.string.label_deck_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Box {
                var showCollections by remember { mutableStateOf(false) }
                OutlinedCard(
                    onClick = { showCollections = !showCollections },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.padding.small)
                    ) {
                        Text(
                            text = if (selectedCollection == null) stringResource(R.string.info_no_collection_choose)
                            else selectedCollection!!.name,
                            modifier = Modifier
                                .weight(1f)
                                .secondaryItemAlpha()
                        )
                        Icon(painterResource(R.drawable.ic_arrow_down), null)
                    }
                }
                DropdownMenu(
                    expanded = showCollections,
                    onDismissRequest = { showCollections = false },
                    shape = MaterialTheme.shapes.small
                ) {
                    collections.forEach { collection ->
                        DropdownMenuItem(
                            text = { Text(text = collection.name) },
                            onClick = {
                                selectedCollection = collection
                                showCollections = false
                            },
                        )
                    }
                    var createCollection by remember { mutableStateOf(false) }
                    DropdownMenuItem(
                        text = { Text(text = "Create new collection") },
                        onClick = {
                            createCollection = true
                        },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_add), null) },
                    )
                    if (createCollection) {
                        TextFieldDialog(
                            onDismiss = { createCollection = false },
                            onDone = {
                                action(HomeUiAction.CreateNewCollection(it))
                            }
                        )
                    }
                }
            }

            Button(
                enabled = selectedCollection != null && name.isNotEmpty(),
                onClick = {
                    action(HomeUiAction.CreateNewDeck(name, selectedCollection!!.id))
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.action_add_deck),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

private data class HomeAddOption(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
)

private val homeAddOptions = listOf(
    HomeAddOption(R.drawable.ic_note_add, R.string.action_add_cards),
    HomeAddOption(R.drawable.ic_create_new_folder, R.string.action_add_deck)
)

private enum class DeckOptions {
    Edit, Config, Delete
}