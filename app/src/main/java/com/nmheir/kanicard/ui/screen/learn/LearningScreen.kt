@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSwipeableCardApi::class)

package com.nmheir.kanicard.ui.screen.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.components.swipe.rememberSwipeableCardState
import com.nmheir.kanicard.core.presentation.components.swipe.swipeableCard
import com.nmheir.kanicard.core.presentation.utils.ExperimentalSwipeableCardApi
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.enums.State
import com.nmheir.kanicard.extensions.timeUntilDue
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.card.InteractiveFlashcard
import com.nmheir.kanicard.ui.screen.Screens
import com.nmheir.kanicard.ui.viewmodels.LearningAction
import com.nmheir.kanicard.ui.viewmodels.LearningData
import com.nmheir.kanicard.ui.viewmodels.LearningViewModel
import timber.log.Timber
import java.time.LocalDateTime


@Composable
fun LearningScreen(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: LearningViewModel = hiltViewModel()
) {
    val deck by viewModel.deck.collectAsStateWithLifecycle()
    val datas by viewModel.datas.collectAsStateWithLifecycle()
    val stateCount by viewModel.stateCount.collectAsStateWithLifecycle()
    val ratingDueInfo by viewModel.ratingDueInfo.collectAsStateWithLifecycle()
    val isCompleteLearning by viewModel.isCompleteLearning.collectAsStateWithLifecycle()
    val haveData by viewModel.haveData.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(isCompleteLearning) {
        if (isCompleteLearning) {
            navController.navigate(Screens.Base.CompleteLearn.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = deck?.name ?: "Unknown Deck",
                onBack = {
                    navController.popBackStack()
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { pv ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(pv)
        ) {
            if (haveData == false) {
                Text(
                    text = "No data to learn"
                )
            }
            if (datas.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StateCountSection(
                        data = stateCount
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)          // <-- quan trọng: cho Box chiếm toàn bộ remaining space
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LearningSection(
                            datas = datas,
                            ratingDue = ratingDueInfo,
                            action = viewModel::onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StateCountSection(
    data: Map<State, Int>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        data.filter { it.key != State.Relearning }.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = it.key.name)
                Text(text = it.value.toString())
            }
        }
    }
}

@Composable
private fun LearningSection(
    datas: List<LearningData>,
    ratingDue: Map<Rating, LocalDateTime>,
    action: (LearningAction) -> Unit
) {
    var showRating by remember { mutableStateOf(false) }
    var ratingChoose by remember { mutableStateOf<Rating?>(null) }
    LaunchedEffect(datas) {
        showRating = false
        ratingChoose = null
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .hozPadding()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            LearnFlashcard(
                data = datas[0],
                showRating = showRating,
                onRatingChoose = ratingChoose,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)

            )
        }

        AnimatedContent(targetState = showRating, label = "ShowRatingSwitcher") { visible ->
            if (!visible) {
                TextButton(
                    onClick = {
                        showRating = true
                        Timber.d("Id: %s", datas[0].id.toString())
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Show answer")
                }
            } else {
                LearningBottomBar(
                    ratingDue = ratingDue,
                    onClick = { rating ->
                        showRating = false
                        ratingChoose = rating
                        action(LearningAction.SubmitReview(datas[0].id, rating))
                    }
                )
            }
        }
    }
}

@Composable
private fun LearnFlashcard(
    modifier: Modifier = Modifier,
    data: LearningData,
    showRating: Boolean,
    onRatingChoose: Rating?
) {
    val flipController = rememberFlipController()
    val swipeState = rememberSwipeableCardState()
    val enableFlip by remember(showRating) {
        derivedStateOf { showRating == true }
    }

    LaunchedEffect(showRating) {
        if (showRating == true) {
            flipController.flipToBack()
        } else {
            flipController.flipToFront()
        }
    }

    /* LaunchedEffect(onRatingChoose) {
         if (onRatingChoose != null) {
             swipeState.swipe(
                 direction  = when (onRatingChoose) {
                     Rating.Again -> SwipeDirection.Left
                     Rating.Hard -> SwipeDirection.Left
                     Rating.Good -> SwipeDirection.Right
                     Rating.Easy -> SwipeDirection.Right
                 }
             )
         }
     }*/

    InteractiveFlashcard(
        modifier = Modifier
            .swipeableCard(
                state = swipeState,
                onSwiped = {},
                blockedDirections = emptyList()
            ),
        flipController = flipController,
        qHtml = data.noteData.qHtml,
        aHtml = data.noteData.aHtml,
        enableFlip = enableFlip
    )
}

@Composable
private fun LearningBottomBar(
    ratingDue: Map<Rating, LocalDateTime>,
    onClick: (Rating) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Rating.entries.fastForEach { rating ->
            val dueTime = ratingDue[rating]
            val formattedDue = dueTime?.timeUntilDue() ?: "No time"

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onClick(rating) }
                    .weight(1f)
                    .background(color = rating.toColor())
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = rating.name,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = formattedDue,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Composable
private fun Rating.toColor(): Color {
    return when (this) {
        Rating.Again -> MaterialTheme.colorScheme.primaryContainer
        Rating.Hard -> MaterialTheme.colorScheme.errorContainer
        Rating.Good -> MaterialTheme.colorScheme.secondaryContainer
        Rating.Easy -> MaterialTheme.colorScheme.tertiaryContainer
    }
}

@Composable
private fun State.toColor(): Color {
    return when (this) {
        State.Relearning -> MaterialTheme.colorScheme.primaryContainer
        State.Learning -> MaterialTheme.colorScheme.secondaryContainer
        State.Review -> MaterialTheme.colorScheme.tertiaryContainer
        State.New -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    val controller = rememberFlipController()
    Flippable(
        flipController = controller,
        frontSide = {
            Text(
                text = "Front side",
            )
        },
        backSide = {
            Text(
                text = "Back side"
            )
        }
    )
}