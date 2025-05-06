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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.data.dto.deck.DeckDto
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.DeckItem
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.HideOnScrollFAB
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.HomeAction
import com.nmheir.kanicard.ui.viewmodels.HomeCategory
import com.nmheir.kanicard.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()


    val lazyListState = rememberLazyListState()

    val homeCategories = HomeCategory.entries
    var showOption by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.padding.small)
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = refreshState,
                onRefresh = viewModel::refresh
            )
    ) {
//        HomeContent(
//            navController = navController,
//            lazyListState = lazyListState,
//            homeCategories = homeCategories,
//            selectedCategory = selectedHomeCategory,
//            myDecks = myDecks.orEmpty(),
//            allDecks = allDecks.orEmpty(),
////            importedDecks = importedDecks.orEmpty(),
//            action = viewModel::onAction,
//        )

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
private fun HomeContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    lazyListState: LazyListState,
    homeCategories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    myDecks: List<DeckDto>,
    allDecks: List<DeckDto>,
//    importedDecks: List<DownloadedDeckEntity>,
    action: (HomeAction) -> Unit
) {
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
        contentPadding = LocalAwareWindowInset.current.asPaddingValues(),
        modifier = modifier
    ) {
//        My latest review section
        item {
            TextPreferenceWidget(
                title = stringResource(R.string.pref_category_latest_review),
                modifier = Modifier.fillMaxWidth()
            )
        }

        //Tab
        item {
            HomeCategoryTabs(
                categories = homeCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { action(HomeAction.HomeCategorySelected(it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        when (selectedCategory) {
            HomeCategory.MY_DECK -> {
                items(
                    items = myDecks,
                    key = { it.id }
                ) {
                    DeckItem(
                        onClick = { deckId ->
                            navController.navigate("deck/$deckId")
                        },
                        deck = it
                    )
                    Spacer(Modifier.height(MaterialTheme.padding.extraSmall))
                }
            }

            HomeCategory.DOWNLOADED -> {
                /*if (importedDecks.isEmpty()) {
                    item {
                        EmptyScreen(R.string.information_empty_deck)
                    }
                } else {
                    items(
                        items = importedDecks,
                        key = { it.id }
                    ) {
                        DeckItem(onClick = {}, deck = it.toDeckDto())
                        Spacer(Modifier.height(MaterialTheme.padding.extraSmall))
                    }
                }*/
            }

            HomeCategory.ALL -> {
                items(
                    items = allDecks,
                    key = { it.id }
                ) {
                    DeckItem(onClick = {}, deck = it)
                    Spacer(Modifier.height(MaterialTheme.padding.extraSmall))
                }
            }
        }
    }
}

@Composable
private fun MyLatestReview(modifier: Modifier = Modifier) {
    // TODO: Need to do
}

@Composable
private fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        categories.fastForEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            HomeCategory.MY_DECK -> stringResource(R.string.pref_category_my_deck)
                            HomeCategory.DOWNLOADED -> stringResource(R.string.pref_category_import_deck)
                            HomeCategory.ALL -> stringResource(R.string.pref_category_all_deck)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
            )
        }
    }
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