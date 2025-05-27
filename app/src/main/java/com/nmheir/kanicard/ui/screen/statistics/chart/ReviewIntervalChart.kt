package com.nmheir.kanicard.ui.screen.statistics.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewIntervalChartData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewIntervalChartState
import com.nmheir.kanicard.ui.screen.statistics.model.core.columnColors
import com.nmheir.kanicard.ui.screen.statistics.model.core.lineColor
import com.nmheir.kanicard.ui.screen.statistics.rememberMarker
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.StatisticUiAction
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.horizontalGradient
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.coroutines.runBlocking

@SuppressLint("DefaultLocale")
@Composable
fun ReviewIntervalChart(
    modifier: Modifier = Modifier,
    data: ReviewIntervalChartData,
    state: ReviewIntervalChartState,
    action: (StatisticUiAction) -> Unit
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        if (data == ReviewIntervalChartData()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries {
                series(data.barData.keys, data.barData.values)
            }
            lineSeries {
                series(data.lineData.keys, data.lineData.values)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {

        Text(
            text = "Delays until review cards are shown again.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        SingleChoiceSegmentedButtonRow {
            ReviewIntervalChartState.entries.forEachIndexed { idx, chartState ->
                SegmentedButton(
                    selected = chartState == state,
                    onClick = {
                        action(StatisticUiAction.ChangeReviewIntervalChartState(chartState))
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = idx,
                        count = ReviewIntervalChartState.entries.size
                    ),
                    label = {
                        Text(
                            text = chartState.title,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }

        }
        ReviewIntervalChart(
            modelProducer = modelProducer
        )
        Text(
            text = pluralStringResource(
                R.plurals.n_average_interval,
                data.avgIvl.toInt(),
                String.format("%.2f", data.avgIvl)
            )
        )
    }
}

@Composable
private fun ReviewIntervalChart(
    modifier: Modifier = Modifier,
    modelProducer: CartesianChartModelProducer
) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(columnColors[3]),
                        thickness = 16.dp
                    )
                ),
                verticalAxisPosition = Axis.Position.Vertical.Start
            ),
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(
                            fill = fill(lineColor)
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic(),
                        areaFill =
                            LineCartesianLayer.AreaFill.single(
                                fill(
                                    ShaderProvider.horizontalGradient(
                                        arrayOf(
                                            lineColor,
                                            lineColor.copy(alpha = 0.1f)
                                        ),
                                    ),
                                ),
                            ),
                    )
                ),
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null
            ),
            endAxis = VerticalAxis.rememberEnd(
                guideline = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null
            ),
            marker = rememberMarker(showIndicator = false)
        ),
        scrollState = rememberVicoScrollState(
            initialScroll = Scroll.Absolute.Start,
            autoScrollCondition = AutoScrollCondition.OnModelGrowth
        ),
        modelProducer = modelProducer,
        placeholder = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.matchParentSize()
            ) {
                Text(
                    text = "No Data",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        },
        modifier = modifier
    )


}

val fakeReviewIntervalChartData = ReviewIntervalChartData(
    barData = mapOf(
        1 to 4,
        2 to 6,
        3 to 5,
        4 to 3,
        5 to 1,
        6 to 1,
        7 to 0
    ),
    lineData = mapOf(
        1 to 16.7,
        2 to 41.7,
        3 to 66.7,
        4 to 83.3,
        5 to 91.7,
        6 to 100.0,
        7 to 100.0
    ),
    avgIvl = 2.75
)

@Preview(showBackground = true)
@Composable
private fun Test() {

    val modelProducer = remember { CartesianChartModelProducer() }

    val barData = fakeReviewIntervalChartData.barData
    val lineData = fakeReviewIntervalChartData.lineData
    val avgIvl = fakeReviewIntervalChartData.avgIvl

    runBlocking {
        modelProducer.runTransaction {
            columnSeries {
                series(barData.keys, barData.values)
            }
            lineSeries {
                series(lineData.keys, lineData.values)
            }
        }
    }

    KaniTheme {
        ReviewIntervalChart(
            modelProducer = modelProducer
        )
    }
}