package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.ui.screen.statistics.model.AnswerButtonChartCardType
import com.nmheir.kanicard.ui.screen.statistics.model.AnswerButtonChartData
import com.nmheir.kanicard.ui.screen.statistics.model.AnswerButtonChartState
import com.nmheir.kanicard.ui.screen.statistics.rememberMarker
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.StatisticUiAction
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.grouped
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking

@Composable
fun AnswerButtonChart(
    modifier: Modifier = Modifier,
    data: AnswerButtonChartData,
    state: AnswerButtonChartState,
    action: (StatisticUiAction) -> Unit
) {

    val modelProducer = remember { CartesianChartModelProducer() }

    val seriesData by remember(data) {
        derivedStateOf {
            data.toRatingSeries()
        }
    }

    LaunchedEffect(data) {
        if (data == AnswerButtonChartData()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries {
                seriesData.map {
                    series(it.value)
                }
            }
            extras {
                it[BottomAxisLabelKey] = data.barData.keys.map { it.name }
                it[LegendLabelKey] = seriesData.keys.map { it.toString() }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        SingleChoiceSegmentedButtonRow {
            AnswerButtonChartState.entries.fastForEachIndexed { idx, chartState ->
                SegmentedButton(
                    selected = chartState == state,
                    shape = SegmentedButtonDefaults.itemShape(
                        index = idx,
                        count = AnswerButtonChartState.entries.size
                    ),
                    onClick = {
                        action(StatisticUiAction.ChangeAnswerButtonChartState(chartState))
                    },
                    label = {
                        Text(
                            text = chartState.title
                        )
                    }
                )
            }
        }

        AnswerButtonChart(
            modelProducer = modelProducer
        )
    }

}

private val LegendLabelKey = ExtraStore.Key<List<String>>()
private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()
private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}

@Composable
private fun AnswerButtonChart(
    modelProducer: CartesianChartModelProducer
) {
    val columnColors = listOf(
        Color(0xff6438a7),
        Color(0xff3490de),
        Color(0xff73e8dc),
        Color.Cyan
    )

    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    columnColors.map {
                        rememberLineComponent(
                            fill = fill(it),
                            thickness = 8.dp
                        )
                    }
                ),
                mergeMode = {
                    ColumnCartesianLayer.MergeMode.grouped(columnSpacing = 2.dp)
                }
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = BottomAxisValueFormatter
            ),
            marker = rememberMarker(showIndicator = false),
            legend = rememberHorizontalLegend(
                items = { extraStore ->
                    extraStore[LegendLabelKey].forEachIndexed { index, label ->
                        add(
                            LegendItem(
                                shapeComponent(
                                    fill(columnColors[index]),
                                    CorneredShape.Pill,
                                ),
                                legendItemLabelComponent,
                                label,
                            ),
                        )
                    }
                }
            )
        ),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        modelProducer = modelProducer
    )
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    val modelProducer = remember { CartesianChartModelProducer() }

    val data = AnswerButtonChartData(
        barData = mapOf(
            AnswerButtonChartCardType.Learning to mapOf(
                Rating.Again to 10,
                Rating.Hard to 20,
                Rating.Good to 30,
                Rating.Easy to 40
            ),
            AnswerButtonChartCardType.Mature to mapOf(
                Rating.Again to 15,
                Rating.Hard to 25,
                Rating.Good to 35,
                Rating.Easy to 45
            ),
            AnswerButtonChartCardType.Young to mapOf(
                Rating.Again to 12,
                Rating.Hard to 22,
                Rating.Good to 32,
                Rating.Easy to 42
            )
        )
    )

    val series = data.toRatingSeries()

    runBlocking {
        modelProducer.runTransaction {
            columnSeries {
                series.map {
                    series(it.value)
                }
            }
            extras {
                it[BottomAxisLabelKey] = data.barData.keys.map { it.name }
                it[LegendLabelKey] = series.keys.map { it.toString() }
            }
        }
    }

    KaniTheme {
        AnswerButtonChart(
            modelProducer
        )
    }
}