package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartState
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartCardData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartData
import com.nmheir.kanicard.ui.screen.statistics.model.ReviewChartState
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
import com.patrykandpatrick.vico.compose.cartesian.layer.stacked
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.horizontalGradient
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun ReviewChart(
    modifier: Modifier = Modifier,
    data: ReviewChartData,
    state: ReviewChartState,
    action: (StatisticUiAction) -> Unit
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val seriesMap by remember(data.barData) {
        derivedStateOf {
            val sorted = data.barData
                .toSortedMap()        // sort theo ngày (Int key)
                .values
                .toList()

            mapOf(
                "Learning" to sorted.map { it.learning },
                "Relearning" to sorted.map { it.relearning },
                "Young" to sorted.map { it.young },
                "Mature" to sorted.map { it.mature }
            )
        }
    }

    LaunchedEffect(data) {
        if (data == ReviewChartData()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries {
                seriesMap.values.forEach {
                    series(data.barData.keys, it)
                }
            }
            lineSeries {
                series(data.lineData.keys, data.lineData.values)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .hozPadding()
    ) {
        Text(
            text = "The number of questions you have answered.",
            style = MaterialTheme.typography.labelSmall
        )

        MultiChoiceSegmentedButtonRow {
            ReviewChartState.entries.fastForEachIndexed { idx, chartState ->
                SegmentedButton(
                    checked = chartState == state,
                    onCheckedChange = {
                        action(StatisticUiAction.ChangeReviewCHartState(chartState))
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = idx,
                        count = FutureDueChartState.entries.size
                    ),
                    label = {
                        Text(text = chartState.title)
                    }
                )
            }
        }


        ReviewChart(
            modelProducer = modelProducer
        )
    }

}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

@Composable
private fun ReviewChart(
    modifier: Modifier = Modifier,
    modelProducer: CartesianChartModelProducer
) {

    val lineColor = Color(0xffa485e0).copy(alpha = 0.4f)

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
                    ColumnCartesianLayer.MergeMode.stacked()
                },
                rangeProvider = CartesianLayerRangeProvider.auto(),
                verticalAxisPosition = Axis.Position.Vertical.Start
            ),
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(
                            fill = fill(lineColor)
                        ),
                        areaFill =
                            LineCartesianLayer.AreaFill.single(
                                fill(
                                    ShaderProvider.horizontalGradient(
                                        arrayOf(
                                            lineColor,
                                            lineColor
                                        ),
                                    ),
                                ),
                            ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic(),
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.auto(),
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            marker = rememberMarker(),
            startAxis = VerticalAxis.rememberStart(),
            endAxis = VerticalAxis.rememberEnd(),
            bottomAxis = HorizontalAxis.rememberBottom(),
//            legend = rememberVerticalLegend(
//                items = { extraStore ->
//                    extraStore[LegendLabelKey].forEachIndexed { index, label ->
//                        add(
//                            LegendItem(
//                                shapeComponent(fill(columnColors[index]), CorneredShape.Pill),
//                                legendItemLabelComponent,
//                                label,
//                            )
//                        )
//                    }
//                },
//                padding = Insets(startDp = 12f)
//            )
        ),
        modelProducer = modelProducer
    )
}

val mock7Large = ReviewChartData(
    barData = mapOf(
        // index: ReviewChartCardData(learning, relearning, young, mature)
        0 to ReviewChartCardData(
            learning = 10,
            relearning = 5,
            young = 15,
            mature = 2
        ),  // tổng = 32
        1 to ReviewChartCardData(
            learning = 8,
            relearning = 7,
            young = 10,
            mature = 5
        ),  // tổng = 30
        2 to ReviewChartCardData(
            learning = 12,
            relearning = 3,
            young = 20,
            mature = 10
        ), // tổng = 45
        3 to ReviewChartCardData(
            learning = 0,
            relearning = 0,
            young = 0,
            mature = 0
        ),  // ngày không học
        4 to ReviewChartCardData(
            learning = 15,
            relearning = 10,
            young = 5,
            mature = 0
        ),  // tổng = 30
        5 to ReviewChartCardData(
            learning = 5,
            relearning = 12,
            young = 18,
            mature = 2
        ),  // tổng = 37
        6 to ReviewChartCardData(
            learning = 20,
            relearning = 8,
            young = 15,
            mature = 5
        )   // tổng = 48
    ),
    // cumulative lineData: 32, 62, 107, 107, 137, 174, 222
    lineData = mapOf(
        0 to 32,
        1 to 62,
        2 to 107,
        3 to 107,
        4 to 137,
        5 to 174,
        6 to 222
    ),
    dayStudied = 6,               // 6 ngày có >=1 review
    total = 222,             // tổng reviews cả kỳ
    averageDayStudied = 222.0 / 6,       // ~37.0 reviews/ngày đã học
    averageOverPeriod = 222.0 / 7        // ~31.71 reviews/ngày toàn kỳ
)


@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        val modelProducer = remember { CartesianChartModelProducer() }
        val barData = mock7Large.barData
        val lineData = mock7Large.lineData

        val seriesMap: Map<String, List<Int>> = barData
            .toSortedMap()                       // sắp xếp key Int tăng dần
            .values                              // Collection<ReviewChartCardData>
            .toList()                            // List<ReviewChartCardData>
            .let { list ->
                mapOf(
                    "learning" to list.map { it.learning },
                    "relearning" to list.map { it.relearning },
                    "young" to list.map { it.young },
                    "mature" to list.map { it.mature }
                )
            }

        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    seriesMap.values.forEach {
                        series(barData.keys, it)
                    }
                }
                lineSeries {
                    series(lineData.keys, lineData.values)
                }

                extras { extraStore ->
                    extraStore[LegendLabelKey] = seriesMap.keys
                }
            }
        }

        ReviewChart(
            modelProducer = modelProducer
        )
    }
}