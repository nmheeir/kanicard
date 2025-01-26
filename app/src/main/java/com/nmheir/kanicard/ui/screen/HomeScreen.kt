package com.nmheir.kanicard.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.DeckItem
import com.nmheir.kanicard.ui.viewmodels.HomeViewModel
import com.nmheir.kanicard.utils.fakeListDecks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val refreshState = rememberPullToRefreshState()

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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = LocalAwareWindowInset.current.asPaddingValues(),
        ) {
            items(
                items = fakeListDecks,
                key = {
                    it.id
                }
            ) {
                DeckItem(
                    onClick = {},
                    deck = it
                )
            }
        }

        Indicator(
            isRefreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(LocalAwareWindowInset.current.asPaddingValues()),
        )
    }
}