@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSwipeableCardApi::class)

package com.nmheir.kanicard.ui.screen.learn

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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.nmheir.kanicard.extensions.format3
import com.nmheir.kanicard.ui.component.TopAppBar
import com.nmheir.kanicard.ui.component.card.ReviewFlashCard
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.LearningAction
import com.nmheir.kanicard.ui.viewmodels.LearningData
import com.nmheir.kanicard.ui.viewmodels.LearningViewModel
import com.nmheir.kanicard.utils.fakeStateCount
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.OffsetDateTime


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

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = deck?.name ?: "Unknown Deck",
                onBack = {
                    navController.popBackStack()
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (datas.isNotEmpty()) {
                LearningBottomBar(
                    ratingDue = ratingDueInfo,
                    onClick = {
                        scope.launch {
                            val id = datas[0].id
                            viewModel.onAction(
                                LearningAction.SubmitReview(
                                    nId = id,
                                    rating = it
                                )
                            )
                        }
                    }
                )
            }
        }
    ) { pv ->
        /*LazyColumn(
            contentPadding = pv,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(
                key = "state_count"
            ) {
                StateCountSection(data = stateCount)
            }

            item(
                key = "learn"
            ) {
                LearningSection(
                    datas = datas
                )
            }
        }*/

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(pv)
        ) {
            StateCountSection(
                data = stateCount
            )

            Box(
                modifier = Modifier
                    .weight(1f)          // <-- quan trọng: cho Box chiếm toàn bộ remaining space
                    .fillMaxWidth()
                    .hozPadding(),
                contentAlignment = Alignment.Center
            ) {
                LearningSection(datas = datas)
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
) {
    // Stack flashcards để card đầu tiên (cuối trong list) nằm trên cùng
    Box(
        modifier = Modifier
            .fillMaxSize()
            .hozPadding(),
        contentAlignment = Alignment.Center
    ) {
        datas.reversed().forEach { data ->
            LearnFlashcard(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .hozPadding()
                    .align(Alignment.Center) // giữ tâm
            )
        }
    }
}

@Composable
private fun LearnFlashcard(
    modifier: Modifier = Modifier,
    data: LearningData
) {
    Timber.d("Learning Note Data: %s", data.noteData.toString())
    val flipController = rememberFlipController()
    val swipeState = rememberSwipeableCardState()

    Flippable(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .swipeableCard(
                state = swipeState,
                onSwiped = {}
            ),
        frontSide = {
            ReviewFlashCard(
                modifier = Modifier
                    .padding(12.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = MaterialTheme.shapes.medium
                    ),
                html = data.noteData.qFmt
            )
        },
        backSide = {
            ReviewFlashCard(
                modifier = Modifier
                    .padding(12.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = MaterialTheme.shapes.medium
                    ),
                html = data.noteData.aFmt
            )
        },
        flipController = flipController
    )
}

@Composable
private fun LearningBottomBar(
    ratingDue: Map<Rating, OffsetDateTime>,
    onClick: (Rating) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Rating.entries.fastForEach { rating ->
            val dueTime = ratingDue[rating]
            val formattedDue = dueTime?.format3() ?: "No time"

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onClick(rating) }
                    .weight(1f)
                    .background(color = rating.toColor())
                    .padding(vertical = 24.dp)
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


@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        StateCountSection(
            data = fakeStateCount
        )
    }
}