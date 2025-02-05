package com.nmheir.kanicard.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.ScrollbarLazyColumn
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.core.presentation.screens.EmptyScreen
import com.nmheir.kanicard.core.presentation.screens.EmptyScreenAction
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.data.dto.DeckDetailDto
import com.nmheir.kanicard.data.dto.DeckDto
import com.nmheir.kanicard.data.dto.ProfileDto
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.flip.CardHeight
import com.nmheir.kanicard.ui.component.flip.SampleFlipCardBackSide
import com.nmheir.kanicard.ui.component.flip.SampleFlipCardFrontSide
import com.nmheir.kanicard.ui.component.image.CoilImage
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.DeckDetailViewModel
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: DeckDetailViewModel = hiltViewModel()
) {

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val deckDetail by viewModel.deckDetail.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val isDeckImported by viewModel.isDeckImported.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                    cards = cards,
                    deckDetail = deckDetail!!,
                    loadMore = { },
                    onBack = navController::navigateUp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckDetailContent(
    cards: List<CardDto>?,
    deckDetail: DeckDetailDto,
    loadMore: () -> Unit,
    onBack: () -> Unit
) {
    val deck = remember(deckDetail) { deckDetail.toDeck() }
    val profile = remember(deckDetail) { deckDetail.profileDto }
    val cardsInDeck = remember(cards) { cards }

    val lazyListState = rememberLazyListState()
    val cardListState = rememberLazyGridState()

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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        ScrollbarLazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
        ) {
            //Deck detail header item
            item {
                DeckDetailHeaderItem(
                    deck = deckDetail.toDeck(),
                    profile = profile
                )
            }

            item {
                TextPreferenceWidget(title = "Sample Card")
            }

            if (cardsInDeck.isNullOrEmpty()) {
                item {
                    TextPreferenceWidget(title = "No cards")
                }
            } else {
                item {
                    LazyHorizontalGrid(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                        contentPadding = PaddingValues(horizontal = MaterialTheme.padding.medium),
                        state = cardListState,
                        rows = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CardHeight)
                    ) {
                        items(
                            items = cardsInDeck,
                            key = { it.id }
                        ) {
                            val flipController = rememberFlipController()
                            Flippable(
                                frontSide = {
                                    SampleFlipCardFrontSide(flipController, it)
                                },
                                backSide = {
                                    SampleFlipCardBackSide(flipController, it)
                                },
                                flipController = flipController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckDetailHeaderItem(
    modifier: Modifier = Modifier,
    deck: DeckDto,
    profile: ProfileDto
) {
    BoxWithConstraints(modifier = modifier) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min(maxImageSize, 112.dp)

        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.padding.medium)
            ) {
                if (deck.thumbnail == null) {
                    Image(
                        painter = painterResource(R.drawable.ic_error),
                        contentDescription = null,
                        modifier = Modifier
                            .size(imageSize)
                            .clip(MaterialTheme.shapes.large)
                    )
                } else {
                    CoilImage(
                        imageUrl = deck.thumbnail,
                        modifier = Modifier
                            .size(imageSize)
                            .clip(MaterialTheme.shapes.large)
                    )
                }
                Column(
                    modifier = Modifier.padding(start = MaterialTheme.padding.medium)
                ) {
                    Text(
                        text = deck.title,
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

            DeckCreator(profile = profile)

            DeckDetailDescription(description = deck.description ?: "No description")
        }
    }
}

@Composable
private fun DeckCreator(
    modifier: Modifier = Modifier,
    profile: ProfileDto
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TextPreferenceWidget(title = "Author")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.padding.medium)
        ) {
            CoilImage(
                imageUrl = profile.avatarUrl ?: "",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Gap(MaterialTheme.padding.small)
            Text(
                text = profile.userName ?: "Unknown user",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
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
        modifier = modifier.padding(top = MaterialTheme.padding.medium)
    ) {
        Button(
            enabled = !isImported,
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                painter = painterResource(
                    if (isImported) R.drawable.ic_check else R.drawable.ic_download
                ),
                contentDescription = null
            )
            Gap(MaterialTheme.padding.small)
            Text(
                text = stringResource(
                    if (!isImported) R.string.action_download else R.string.label_already_downloaded
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Gap(width = MaterialTheme.padding.medium)

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
    description: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSeeMore by remember { mutableStateOf(false) }

    Column {
        TextPreferenceWidget(title = stringResource(R.string.label_description))
        Box(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.padding.medium)
                .clickable { isExpanded = !isExpanded }
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

}