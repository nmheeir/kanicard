@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.nmheir.kanicard.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.screens.LoadingScreen
import com.nmheir.kanicard.core.presentation.utils.extractMembers
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.component.button.IconButtonDropdownMenu
import com.nmheir.kanicard.ui.component.dialog.ListOptionDialog
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.BrowseCardUiAction
import com.nmheir.kanicard.ui.viewmodels.BrowseCardUiData
import com.nmheir.kanicard.ui.viewmodels.BrowseCardViewModel
import com.nmheir.kanicard.ui.viewmodels.BrowseOption
import com.nmheir.kanicard.ui.viewmodels.SortType
import com.nmheir.kanicard.ui.viewmodels.toQstValuePairs
import com.nmheir.kanicard.utils.fakeCardBrowseDatas
import kotlinx.coroutines.delay

@Composable
fun BrowseCardScreen(
    navController: NavHostController,
    viewModel: BrowseCardViewModel = hiltViewModel()
) {
    val deck by viewModel.deck.collectAsStateWithLifecycle()
    val headerOption by viewModel.headerOption.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val data by viewModel.data.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedNote by viewModel.selectedNote.collectAsStateWithLifecycle()
    val filterData by viewModel.filterData.collectAsStateWithLifecycle()

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    val focusRequest = remember { FocusRequester() }
    LaunchedEffect(isSearching) {
        if (isSearching)
            focusRequest.requestFocus()
    }

    BackHandler {
        if (isSearching) isSearching = false
        if (inSelectMode) inSelectMode = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (inSelectMode) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.n_selected,
                                selectedNote.size,
                                selectedNote.size
                            )
                        )
                    } else if (isSearching) {
                        TextField(
                            value = query,
                            onValueChange = {
                                query = it
                                viewModel.onQueryChange(it)
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequest)
                        )
                    } else {
                        Text(
                            text = deck?.name ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                navigationIcon = {
                    if (inSelectMode) {
                        IconButton(
                            onClick = { inSelectMode = false }
                        ) {
                            Icon(painterResource(R.drawable.ic_close), null)
                        }
                    } else {
                        IconButton(
                            onClick = navController::navigateUp
                        ) {
                            Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                        }
                    }
                },
                actions = {
                    if (inSelectMode) {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(painterResource(R.drawable.ic_check), null)
                        }
                    } else {
                        IconButton(
                            onClick = { isSearching = true }
                        ) {
                            Icon(painterResource(R.drawable.ic_search), null)
                        }
                    }
                    IconButtonDropdownMenu(
                        iconRes = R.drawable.ic_more_vert,
                        values = BrowseCardMenuOption.entries,
                        valueText = { it.title },
                        onValueSelected = {
                            when (it) {
                                BrowseCardMenuOption.SELECT_ALL -> {
                                    viewModel.onAction(BrowseCardUiAction.UpdateSelectAll)
                                    inSelectMode = true
                                }

                                BrowseCardMenuOption.PREVIEW -> {

                                }
                            }
                        }
                    )
                }
            )
        }
    ) { pv ->
        if (isLoading) {
            LoadingScreen()
        }
        LazyColumn(
            contentPadding = pv,
            modifier = Modifier.imePadding()
        ) {
            item {
                HeaderSection(
                    headerOption = headerOption,
                    sortType = sortType,
                    inSelectMode = inSelectMode,
                    action = viewModel::onAction
                )
            }
            items(
                items = if (isSearching) filterData else data,
                key = { it.nid }
            ) {
                RowData(
                    data = it,
                    inSelectMode = inSelectMode,
                    action = viewModel::onAction,
                    modifier = Modifier
                        .hozPadding()
                        .combinedClickable(
                            enabled = true,
                            onClick = {
                                //navigate to edit note screen
                                if (inSelectMode) {
                                    viewModel.onAction(BrowseCardUiAction.UpdateSelect(it.nid))
                                }
                            },
                            onLongClick = {
                                inSelectMode = !inSelectMode
                                viewModel.onAction(BrowseCardUiAction.UpdateSelect(it.nid))
                            }
                        ),
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    headerOption: Pair<String, BrowseOption>,
    sortType: SortType,
    inSelectMode: Boolean,
    action: (BrowseCardUiAction) -> Unit
) {
    var showOption by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (inSelectMode) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(painterResource(R.drawable.ic_check), null)
                }
                Text(
                    text = "Select all",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Text(
            text = headerOption.first,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .clickable {
                    showOption = true
                }
        ) {
            Text(
                text = headerOption.second.title,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    action(BrowseCardUiAction.ChangeSortType)
                }
            ) {
                Icon(
                    painterResource(if (sortType == SortType.Ascending) R.drawable.ic_arrow_downward else R.drawable.ic_arrow_upward),
                    null
                )
            }

            if (showOption) {
                ListOptionDialog(
                    onDismiss = { showOption = false },
                    selectedValue = headerOption.second,
                    values = BrowseOption.entries,
                    onValueSelected = {
                        action(BrowseCardUiAction.ChangeBrowseOption(it))
                    },
                    valueText = {
                        it.title
                    }
                )
            }
        }
    }
}

@Composable
private fun RowData(
    modifier: Modifier = Modifier,
    data: BrowseCardUiData,
    inSelectMode: Boolean,
    action: (BrowseCardUiAction) -> Unit
) {
    val itemData = extractMembers(data).filter {
        if (!inSelectMode) {
            it.first != "nid" && it.first != "isSelect"
        } else {
            it.first != "nid"
        }
    }

    val strokeColor = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = 1.dp.toPx()
                val w = size.width
                val h = size.height

                val weights = itemData.map { (attr, _) ->
                    if (attr == "isSelect") 2f else 4f
                }
                val totalWeight = weights.sum()

                var accumulatedWeight = 0f

                // 🔹 Vẽ đường trái ngoài cùng
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, h),
                    strokeWidth = stroke
                )

                // Vẽ đường dọc theo tỉ lệ weight
                for (i in 0..weights.lastIndex) {
                    accumulatedWeight += weights[i]
                    val x = w * (accumulatedWeight / totalWeight)
                    drawLine(
                        color = strokeColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = stroke
                    )
                }


                // Vẽ đường ngang trên
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, 0f),
                    end = Offset(w, 0f),
                    strokeWidth = stroke
                )

                // Vẽ đường ngang dưới
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, h),
                    end = Offset(w, h),
                    strokeWidth = stroke
                )
            }

    ) {
        itemData.forEachIndexed { idx, (attribute, value) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(
                        if (attribute == "isSelect") 2f else 4f
                    )
                    .padding(12.dp)
            ) {
                if (attribute == "isSelect") {
                    Checkbox(
                        checked = data.isSelect,
                        onCheckedChange = {
                            action(BrowseCardUiAction.UpdateSelect(data.nid))
                        }
                    )
                } else {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private enum class BrowseCardMenuOption(val title: String) {
    SELECT_ALL("Select all"),
    PREVIEW("Preview")
}

//@Preview(showBackground = true)
@Composable
private fun Test() {
    val data = fakeCardBrowseDatas.toQstValuePairs(BrowseOption.Answer, SortType.Ascending)
    KaniTheme {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            data.fastForEach {
                RowData(data = it, inSelectMode = true) {

                }
            }
        }
    }
}