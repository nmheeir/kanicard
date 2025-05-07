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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.AlertDialog
import com.nmheir.kanicard.ui.component.DeckItem
import com.nmheir.kanicard.ui.component.DefaultDialog
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.HideOnScrollFAB
import com.nmheir.kanicard.ui.component.ListDialog
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.HomeViewModel
import com.nmheir.kanicard.utils.fakeDeckWidgetData

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

    var showOption by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }

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
            data = fakeDeckWidgetData,
            navController = navController
        )

        HideOnScrollFAB(
            visible = true,
            lazyListState = lazyListState,
            icon = R.drawable.ic_add,
            onClick = { showOption = !showOption }
        )

        AnimatedVisibility(
            visible = showOption,
            modifier = Modifier
                .padding(end = 80.dp) // >= 72 (56 + 16)
                .padding(LocalAwareWindowInset.current.asPaddingValues())
                .align(Alignment.BottomCenter)
        ) {
            OptionDialog(navController = navController)
        }

        /*        if (showCreateDeckDialog) {

                }*/
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    data: List<DeckWidgetData>,
    navController: NavHostController
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
                DeckItem(
                    deck = deckWidgetData,
                    onLearn = {},
                    onEdit = {},
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
                                DeckOptions.Delete -> { showDeleteDialog = true }
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
private fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

@Composable
private fun OptionDialog(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
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

                                R.string.action_add_deck -> {}
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