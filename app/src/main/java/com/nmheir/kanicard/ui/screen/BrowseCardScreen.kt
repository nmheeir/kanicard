@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
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

@Composable
fun BrowseCardScreen(
    navController: NavHostController,
    viewModel: BrowseCardViewModel = hiltViewModel()
) {
    val headerOption by viewModel.headerOption.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val data by viewModel.data.collectAsStateWithLifecycle()

    var showSelectCheckBox by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Browse card")
                },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(painterResource(R.drawable.ic_search), null)
                    }
                    IconButtonDropdownMenu(
                        iconRes = R.drawable.ic_more_vert,
                        values = BrowseCardMenuOption.entries,
                        valueText = { it.title },
                        onValueSelected = {
                            when (it) {
                                BrowseCardMenuOption.SELECT_ALL -> {
                                    viewModel.onAction(BrowseCardUiAction.UpdateSelectAll)
                                    showSelectCheckBox = true
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { pv ->
        LazyColumn(
            contentPadding = pv
        ) {
            item {
                HeaderSection(
                    headerOption = headerOption,
                    sortType = sortType,
                    action = viewModel::onAction
                )
            }
            items(
                items = data,
                key = { it.nid }
            ) {
                RowData(
                    data = it,
                    showSelectCheckBox = showSelectCheckBox,
                    action = viewModel::onAction,
                    modifier = Modifier
                        .hozPadding()
                        .combinedClickable(
                            enabled = true,
                            onClick = {
                                //navigate to edit note screen
                                if (showSelectCheckBox) {
                                    viewModel.onAction(BrowseCardUiAction.UpdateSelect(it.nid))
                                }
                            },
                            onLongClick = {
                                showSelectCheckBox = !showSelectCheckBox
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
    action: (BrowseCardUiAction) -> Unit
) {
    var showOption by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
    showSelectCheckBox: Boolean,
    action: (BrowseCardUiAction) -> Unit
) {
    val itemData = extractMembers(data).filter {
        if (!showSelectCheckBox) {
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
                val colCount = itemData.size
                val stroke = 1.dp.toPx()
                val w = size.width
                val h = size.height

                // Vẽ đường dọc cho tất cả cột, kể cả biên trái và biên phải
                for (i in 0..colCount) {
                    val x = w * i / colCount
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
                    .weight(1f)
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
    SELECT_ALL("Select all")
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    val data = fakeCardBrowseDatas.toQstValuePairs(BrowseOption.Answer, SortType.Ascending)
    KaniTheme {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            data.fastForEach {
                RowData(data = it, showSelectCheckBox = true) {

                }
            }
        }
    }
}