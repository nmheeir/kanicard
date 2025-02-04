package com.nmheir.kanicard.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.screens.EmptyScreen
import com.nmheir.kanicard.core.presentation.screens.EmptyScreenAction
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.image.CoilImage
import com.nmheir.kanicard.ui.viewmodels.DeckDetailViewModel
import com.nmheir.kanicard.utils.fakeCardList
import com.nmheir.kanicard.utils.fakeDeckDetailDto
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeckDetailViewModel = hiltViewModel()
) {

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val continuation by viewModel.continuation.collectAsStateWithLifecycle()

    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val deckDetail by viewModel.deckDetail.collectAsStateWithLifecycle()

    Box {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            deckDetail == null -> {
                EmptyScreen(
                    stringRes = R.string.err_unknown,
                    actions = persistentListOf(
                        EmptyScreenAction(
                            stringRes = R.string.action_retry,
                            icon = R.drawable.ic_refresh,
                            onClick = { viewModel.refresh() }
                        )
                    )
                )
            }

            deckDetail != null -> {
                DeckDetailContent(
                    deckDetail = deckDetail!!,
                    continuation = continuation,
                    cards = cards,
                    loadMore = { },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckDetailContent(
//    navController: NavHostController,
    deckDetail: DeckDetailDto,
    cards: List<CardDto>?,
    continuation: Boolean,
    loadMore: () -> Unit
) {
    val deck = remember(deckDetail) { deckDetail.deckDto }
    val profile = remember(deckDetail) { deckDetail.profileEntity }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "loading" }
        }.collect { shouldLoadMore ->
            if (!shouldLoadMore) return@collect
            loadMore()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark),
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        ScrollbarLazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            //Deck detail header item
            item {
                DeckDetailHeaderItem(deckDetail = deckDetail)
            }
        }
    }
}

@Composable
private fun DeckDetailHeaderItem(
    modifier: Modifier = Modifier,
    deckDetail: DeckDetailDto
) {
    BoxWithConstraints {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min(maxImageSize, 148.dp)

        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                CoilImage(
                    imageUrl = deckDetail.deckDto.thumbnail,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large)
                )
                Column(
                    modifier = Modifier.padding(start = MaterialTheme.padding.medium)
                ) {
                    Text(
                        text = deckDetail.deckDto.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    DeckDetailHeaderItemButton(
                        isImported = false,
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (deckDetail.deckDto.description.isNotEmpty()) {
                DeckDetailDescription(description = deckDetail.deckDto.description)
            }
        }
    }
}

@Composable
private fun DeckDetailHeaderItemButton(
    modifier: Modifier = Modifier,
    isImported: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = MaterialTheme.padding.medium)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isImported)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                painter = painterResource(
                    if (isImported) R.drawable.ic_check else R.drawable.ic_add
                ),
                contentDescription = null
            )
            Text(
                text = stringResource(
                    if (isImported) R.string.action_imported else R.string.action_import
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Gap(width = MaterialTheme.padding.small)

        IconButton(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null
            )
        }
    }

}


@Composable
private fun DeckDetailDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSeeMore by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.clickable { isExpanded = !isExpanded }
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                showSeeMore = result.hasVisualOverflow
            },
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseOutExpo
                )
            )
        )
        if (showSeeMore) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // TODO: Add gradient effect
                Text(
                    text = stringResource(id = R.string.label_see_more),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Test() {
    DeckDetailContent(
        deckDetail = fakeDeckDetailDto,
        cards = fakeCardList,
        continuation = false,
        loadMore = { }
    )
}