package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.FutureDueChartData
import com.nmheir.kanicard.ui.viewmodels.FutureDueChartState
import com.nmheir.kanicard.ui.viewmodels.StatisticUiAction
import com.nmheir.kanicard.ui.viewmodels.fakeFutureDueData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
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
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking

@Composable
fun FutureDueChart(
    modifier: Modifier = Modifier,
    state: FutureDueChartState,
    data: FutureDueChartData,
    action: (StatisticUiAction) -> Unit
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val lineColor = Color(0xffa485e0).copy(alpha = 0.4f)
    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

    LaunchedEffect(data) {
        if (data == FutureDueChartData()) return@LaunchedEffect
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
        modifier = Modifier.hozPadding()
    ) {

        MultiChoiceSegmentedButtonRow {
            FutureDueChartState.entries.fastForEachIndexed { idx, chartState ->
                SegmentedButton(
                    checked = chartState == state,
                    onCheckedChange = {
                        action(StatisticUiAction.ChangeFutureDueChartState(chartState))
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

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = fill(Color(0xffffc002)),
                            thickness = 16.dp,

                            ),
                    ),
                    rangeProvider = CartesianLayerRangeProvider.auto(),
                    verticalAxisPosition = Axis.Position.Vertical.End
                ),
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.double(
                                topFill = fill(lineColor),
                                bottomFill = fill(Color.Red)
                            ),
                            pointConnector = LineCartesianLayer.PointConnector.cubic(),
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
                        ),
                    ),
                    rangeProvider = CartesianLayerRangeProvider.auto(),
                    verticalAxisPosition = Axis.Position.Vertical.Start
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
                endAxis = VerticalAxis.rememberEnd(),
                legend = rememberVerticalLegend(
                    items = {
                        add(
                            LegendItem(
                                shapeComponent(fill(lineColor), CorneredShape.Pill),
                                legendItemLabelComponent,
                                "Average: ${data.average}"
                            )
                        )
                        add(
                            LegendItem(
                                shapeComponent(fill(lineColor), CorneredShape.Pill),
                                legendItemLabelComponent,
                                "Due tomorrow: ${data.dueTomorrow}"
                            )
                        )
                        add(
                            LegendItem(
                                shapeComponent(fill(lineColor), CorneredShape.Pill),
                                legendItemLabelComponent,
                                "Daily load: ${data.dailyLoad}"
                            )
                        )
                    },
                    padding = Insets(horizontalDp = 12f)
                )
            ),
            modelProducer = modelProducer,
            placeholder = {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            },
            modifier = modifier
        )
    }
}

@Composable
private fun FutureDueChart(
    modifier: Modifier = Modifier,
    modelProducer: CartesianChartModelProducer
) {
    val lineColor = Color(0xffa485e0).copy(alpha = 0.4f)

    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(Color(0xffffc002)),
                        thickness = 16.dp,

                        ),
                ),
                rangeProvider = CartesianLayerRangeProvider.auto(),
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.double(
                            topFill = fill(lineColor),
                            bottomFill = fill(Color.Red)
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic(),
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
                    ),
                ),
                rangeProvider = CartesianLayerRangeProvider.auto(),
                verticalAxisPosition = Axis.Position.Vertical.Start
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
            endAxis = VerticalAxis.rememberEnd(),
            legend = rememberVerticalLegend(
                items = {
                    add(
                        LegendItem(
                            shapeComponent(fill(lineColor), CorneredShape.Pill),
                            legendItemLabelComponent,
                            "Due tomorrow 1",
                        )
                    )

                    add(
                        LegendItem(
                            shapeComponent(fill(lineColor), CorneredShape.Pill),
                            legendItemLabelComponent,
                            "Daily load 1"
                        )
                    )

                    add(
                        LegendItem(
                            shapeComponent(fill(lineColor), CorneredShape.Pill),
                            legendItemLabelComponent,
                            "Average 2"
                        )
                    )
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Test() {
    val modelProducer = remember { CartesianChartModelProducer() }

    val data = fakeFutureDueData[FutureDueChartState.ONE_MONTH]!!

    KaniTheme {
        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    series(data.barData.keys, data.barData.values)
                }

                lineSeries {
                    series(data.lineData.keys, data.lineData.values)
                }
            }
        }

        FutureDueChart(
            modelProducer = modelProducer
        )
    }
}