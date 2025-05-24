package com.nmheir.kanicard.ui.screen.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nmheir.kanicard.ui.screen.statistics.chart.CalendarChart
import com.nmheir.kanicard.ui.screen.statistics.chart.FutureDueChart
import com.nmheir.kanicard.ui.viewmodels.StatisticViewModel

@Composable
fun StatisticScreen(
    navController: NavHostController,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val futureDueChartData by viewModel.futureDueChartData.collectAsStateWithLifecycle()
    val futureDueChartState by viewModel.futureDueChartState.collectAsStateWithLifecycle()
    val calendarChartData by viewModel.calendarChartDate.collectAsStateWithLifecycle()
    val calendarChartState by viewModel.calendarChartState.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()

    Scaffold() { pv ->
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = pv,
            modifier = Modifier
                .fillMaxSize()
        ) {

            item(
                key = "future_due_chart"
            ) {
                FutureDueChart(
                    modifier = Modifier,
                    state = futureDueChartState,
                    data = futureDueChartData,
                    action = viewModel::onAction
                )
            }

            item(
                key = "calendar_chart"
            ) {
                CalendarChart(
                    data = calendarChartData,
                    year = calendarChartState,
                    action = viewModel::onAction
                )
            }
        }
    }

}