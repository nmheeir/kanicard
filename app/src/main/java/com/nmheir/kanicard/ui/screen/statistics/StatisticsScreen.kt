package com.nmheir.kanicard.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.activities.LocalAwareWindowInset
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.widget.PreferenceEntry
import com.nmheir.kanicard.ui.component.widget.PreferenceGroupHeader
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.screen.statistics.chart.AnswerButtonChart
import com.nmheir.kanicard.ui.screen.statistics.chart.CalendarChart
import com.nmheir.kanicard.ui.screen.statistics.chart.CardCountChart
import com.nmheir.kanicard.ui.screen.statistics.chart.DifficultyChart
import com.nmheir.kanicard.ui.screen.statistics.chart.FutureDueChart
import com.nmheir.kanicard.ui.screen.statistics.chart.ReviewChart
import com.nmheir.kanicard.ui.screen.statistics.chart.ReviewIntervalChart
import com.nmheir.kanicard.ui.viewmodels.StatisticViewModel
import kotlinx.coroutines.launch

@Composable
fun StatisticScreen(
    navController: NavHostController,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val dueCardsToday by viewModel.dueCardsToday.collectAsStateWithLifecycle()
    val studiedToDay by viewModel.recentlyStudiedCards.collectAsStateWithLifecycle()

    val futureDueChartData by viewModel.futureDueChartData.collectAsStateWithLifecycle()
    val futureDueChartState by viewModel.futureDueChartState.collectAsStateWithLifecycle()

    val calendarChartData by viewModel.calendarChartDate.collectAsStateWithLifecycle()
    val calendarChartState by viewModel.calendarChartState.collectAsStateWithLifecycle()

    val reviewChartData by viewModel.reviewChartData.collectAsStateWithLifecycle()
    val reviewChartState by viewModel.reviewChartState.collectAsStateWithLifecycle()

    val cardCountChartData by viewModel.cardCountChartData.collectAsStateWithLifecycle()

    val reviewIntervalChartData by viewModel.reviewIntervalChartData.collectAsStateWithLifecycle()
    val reviewIntervalChartState by viewModel.reviewIntervalChartState.collectAsStateWithLifecycle()

    val difficultyChartData by viewModel.difficultyChartData.collectAsStateWithLifecycle()

    val answerButtonChartState by viewModel.answerButtonChartState.collectAsStateWithLifecycle()
    val answerButtonChartData by viewModel.answerButtonChartData.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(0) {
        StatisticTab.entries.size
    }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = LocalAwareWindowInset.current.asPaddingValues(),
        modifier = Modifier
            .fillMaxSize()
    ) {

        item(
            key = "Overview"
        ) {
            OverviewSection(
                dueToday = dueCardsToday,
                studiedToday = studiedToDay
            )
        }


        item(
            key = "tab"
        ) {
            Gap(24.dp)
            StatisticTabSection(
                modifier = Modifier
                    .fillMaxWidth(),
                pagerState = pagerState,
                onTabSelected = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }

        item(
            key = "horizontal_page"
        ) {
            HorizontalPager(
                verticalAlignment = Alignment.Top,
                pageSpacing = 12.dp,
                contentPadding = PaddingValues(horizontal = 12.dp),
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
//                    StatisticTab.ALL.ordinal -> {
//
//                    }

                    StatisticTab.FUTURE_DUE.ordinal -> {
                        FutureDueChart(
                            state = futureDueChartState,
                            data = futureDueChartData,
                            action = viewModel::onAction
                        )
                    }

                    StatisticTab.CALENDAR.ordinal -> {
                        CalendarChart(
                            data = calendarChartData,
                            year = calendarChartState,
                            action = viewModel::onAction
                        )
                    }

                    StatisticTab.REVIEWS.ordinal -> {
                        ReviewChart(
                            data = reviewChartData,
                            state = reviewChartState,
                            action = viewModel::onAction
                        )
                    }

                    StatisticTab.CARD_COUNT.ordinal -> {
                        CardCountChart(
                            data = cardCountChartData
                        )
                    }

                    StatisticTab.REVIEW_INTERVAL.ordinal -> {
                        ReviewIntervalChart(
                            data = reviewIntervalChartData,
                            state = reviewIntervalChartState,
                            action = viewModel::onAction
                        )
                    }

                    StatisticTab.CARD_DIFFICULTY.ordinal -> {
                        DifficultyChart(
                            data = difficultyChartData
                        )
                    }

                    StatisticTab.ANSWER_BUTTON.ordinal -> {
                        AnswerButtonChart(
                            data = answerButtonChartData,
                            state = answerButtonChartState,
                            action = viewModel::onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewSection(
    modifier: Modifier = Modifier,
    dueToday: Int,
    studiedToday: Int
) {
    TextPreferenceWidget(
        title = "Overview"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(3f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_due_today)
                )
                Text(
                    text = dueToday.toString()
                )
                Text(
                    text = if (dueToday <= 1) "Card" else "Cards"
                )
            }
        }

        Gap(24.dp)

        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(7f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Studied Today"
                )
                Text(
                    text = studiedToday.toString()
                )
                Text(
                    text = if (studiedToday <= 1) "Card" else "Cards"
                )
            }
        }
    }
}

@Composable
private fun StatisticTabSection(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        edgePadding = 0.dp,
        selectedTabIndex = pagerState.currentPage,
        modifier = modifier
    ) {
        StatisticTab.entries.fastForEachIndexed { idx, tab ->
            Tab(
                selected = idx == pagerState.currentPage,
                onClick = {
                    onTabSelected(idx)
                },
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }

}

/*@Composable
private fun LazyListScope.AllChartSection(
    modifier: Modifier = Modifier
) {
    item {

    }
}*/

enum class StatisticTab(val title: String) {
    //    ALL("All"),
    FUTURE_DUE("Future Due"),
    CALENDAR("Calendar"),
    REVIEWS("Reviews"),
    CARD_COUNT("Card Counts"),
    REVIEW_INTERVAL("Review Intervals"),
    CARD_DIFFICULTY("Card Difficulty"),
    ANSWER_BUTTON("Answer Buttons")
}