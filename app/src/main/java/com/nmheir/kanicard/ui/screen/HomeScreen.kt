package com.nmheir.kanicard.ui.screen

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.screens.EmptyScreen
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.DeckItem
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.HideOnScrollFAB
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val decks by viewModel.decks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()

    val lazyListState = rememberLazyListState()

    var showOption by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = refreshState,
                onRefresh = viewModel::refresh
            )
    ) {
        if (decks?.isEmpty() == true || decks == null) {
            EmptyScreen(stringRes = R.string.information_empty_deck)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            contentPadding = LocalAwareWindowInset.current.asPaddingValues()
        ) {
            decks?.takeIf { it.isNotEmpty() }?.let { listDeck ->
                items(
                    items = listDeck,
                    key = { it.id }
                ) {
                    DeckItem(onClick = {}, deck = it)
                }
            }
        }

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


        Indicator(
            isRefreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(LocalAwareWindowInset.current.asPaddingValues()),
        )
    }
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
                                R.string.action_get_shared_deck -> {}
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
    HomeAddOption(R.drawable.ic_create_new_folder, R.string.action_add_deck),
    HomeAddOption(R.drawable.ic_share, R.string.action_get_shared_deck)
)