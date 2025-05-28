package com.nmheir.kanicard.ui.screen.statistics.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartData
import com.nmheir.kanicard.ui.screen.statistics.model.FutureDueChartState
import com.nmheir.kanicard.ui.screen.statistics.model.core.columnColors
import com.nmheir.kanicard.ui.screen.statistics.model.core.lineColor
import com.nmheir.kanicard.ui.screen.statistics.rememberMarker
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.ui.viewmodels.StatisticUiAction
import com.nmheir.kanicard.utils.fakeFutureDueData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.auto
import com.patrykandpatrick.vico.compose.cartesian.axis.fraction
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.text
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
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun FutureDueChart(
    state: FutureDueChartState,
    data: FutureDueChartData,
    action: (StatisticUiAction) -> Unit
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val decimalFormat = DecimalFormat("#.##")

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

    val avgCount by remember(data) {
        derivedStateOf {
            0
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(12.dp)
    ) {
        Text(
            text = "The number of reviews due in the future.",
            style = MaterialTheme.typography.labelLarge
        )

        Gap(12.dp)

        SingleChoiceSegmentedButtonRow {
            FutureDueChartState.entries.fastForEachIndexed { idx, chartState ->
                SegmentedButton(
                    selected = chartState == state,
                    onClick = {
                        action(StatisticUiAction.ChangeFutureDueChartState(chartState))
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = idx,
                        count = FutureDueChartState.entries.size
                    ),
                    label = {
                        Text(
                            text = chartState.title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }

        FutureDueChart(modelProducer = modelProducer)

        Text(
            text = stringResource(R.string.total) + ": " + pluralStringResource(
                R.plurals.n_reviews,
                data.total,
                data.total
            ),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = stringResource(R.string.average) + ": " + pluralStringResource(
                R.plurals.n_reviews_day,
                avgCount,
                decimalFormat.format(data.average)
            ),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = stringResource(R.string.due_tomorrow) + ": " + pluralStringResource(
                R.plurals.n_reviews_day,
                data.dueTomorrow,
                data.dueTomorrow
            ),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = stringResource(R.string.daily_load) + ": " + pluralStringResource(
                R.plurals.n_reviews_day,
                data.dailyLoad,
                data.dailyLoad
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}


private val BottomAxisItemPlacer = HorizontalAxis.ItemPlacer.aligned(
//    spacing = { 2 },
//    offset = { 1 },
    shiftExtremeLines = false,
    addExtremeLabelPadding = false
)

@Composable
private fun FutureDueChart(
    modifier: Modifier = Modifier,
    modelProducer: CartesianChartModelProducer,
) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(columnColors[3]),
                        thickness = 16.dp,
                    ),
                ),
                columnCollectionSpacing = 8.dp,
                verticalAxisPosition = Axis.Position.Vertical.Start
            ),
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
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
                                            lineColor.copy(alpha = 0.2f)
                                        ),
                                    ),
                                ),
                            ),
                    ),
                ),
                rangeProvider = CartesianLayerRangeProvider.auto(),
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                itemPlacer = remember {
                    BottomAxisItemPlacer
                },
                guideline = null
            ),
            endAxis = VerticalAxis.rememberEnd(
                guideline = null,
            ),
            marker = rememberMarker(showIndicator = false)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Test() {
    val modelProducer = remember { CartesianChartModelProducer() }

    val data = fakeFutureDueData[FutureDueChartState.ONE_MONTH]!!

    KaniTheme {
//        runBlocking {
//            modelProducer.runTransaction {
//                columnSeries {
//                    series(data.barData.keys, data.barData.values)
//                }
//
//                lineSeries {
//                    series(data.lineData.keys, data.lineData.values)
//                }
//                extras { extraStore ->
//                    extraStore[BottomValueKey] = data.barData.keys
//                }
//            }
//        }

        FutureDueChart(
            modelProducer = modelProducer,
        )
    }
}