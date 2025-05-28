package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.ui.screen.statistics.model.DifficultyChartData
import com.nmheir.kanicard.ui.screen.statistics.rememberMarker
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

@Composable
fun DifficultyChart(
    modifier: Modifier = Modifier,
    data: DifficultyChartData
) {

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        if (data == DifficultyChartData()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries {
                series(data.barData.keys, data.barData.values)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = "The higher the difficulty, the slower stability will increase.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        DifficultyChart(
            modelProducer = modelProducer
        )
    }

}


private val BottomAxisItemPlacer = HorizontalAxis.ItemPlacer.aligned(
    spacing = {
        4
    },
    shiftExtremeLines = false,
//    addExtremeLabelPadding = false
)

private val RangeProvider = object : CartesianLayerRangeProvider {
    override fun getMaxY(
        minY: Double,
        maxY: Double,
        extraStore: ExtraStore
    ): Double {
        return 1.2 * maxY
    }
}

private fun getColumnProvider(
    veryEasyColumn: LineComponent,
    easyColumn: LineComponent,
    mediumColumn: LineComponent,
    hardColumn: LineComponent,
    extremelyColumn: LineComponent
) =
    object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore
        ): LineComponent {
            val y = entry.x
            return when {
                y <= 20 -> veryEasyColumn
                y <= 40 -> easyColumn
                y <= 60 -> mediumColumn
                y <= 80 -> hardColumn
                y <= 100 -> extremelyColumn
                else -> veryEasyColumn
            }
        }

        override fun getWidestSeriesColumn(
            seriesIndex: Int,
            extraStore: ExtraStore
        ): LineComponent = mediumColumn
    }

@Composable
private fun DifficultyChart(
    modelProducer: CartesianChartModelProducer
) {

    val color = MaterialTheme.colorScheme.secondaryContainer

    val veryEasyColumn = rememberLineComponent(
        fill = fill(Color(0xFF81C784)), // Màu xanh lá nhạt
        thickness = 16.dp,
    )

    val easyColumn = rememberLineComponent(
        fill = fill(Color(0xFF64B5F6)), // Màu xanh dương nhạt
        thickness = 16.dp,
    )

    val mediumColumn = rememberLineComponent(
        fill = fill(Color(0xFFFFF176)), // Màu vàng nhạt
        thickness = 16.dp,
    )

    val hardColumn = rememberLineComponent(
        fill = fill(Color(0xFFFFB74D)), // Màu cam nhạt
        thickness = 16.dp,
    )

    val extremelyColumn = rememberLineComponent(
        fill = fill(Color(0xFFE57373)), // Màu đỏ nhạt
        thickness = 16.dp,
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider =
                    getColumnProvider(
                        veryEasyColumn,
                        easyColumn,
                        mediumColumn,
                        hardColumn,
                        extremelyColumn
                    ),
                columnCollectionSpacing = 4.dp,
                verticalAxisPosition = Axis.Position.Vertical.Start,
//                rangeProvider = remember {
//                    RangeProvider
//                }
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
                itemPlacer = BottomAxisItemPlacer,
            ),
            marker = rememberMarker()
        ),
        scrollState = rememberVicoScrollState(

        ),
        modelProducer = modelProducer
    )
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    val modelProducer = remember { CartesianChartModelProducer() }

    val barDataLarge1: Map<Int, Int> = buildMap {
        for (bin in 0 until 100 step 5) {
            // mỗi bin có số lượng ngẫu nhiên từ 10 đến 100
            put(bin, Random.nextInt(1000) + 200)
        }
    }


    val totalCount1 = barDataLarge1.values.sum()
    val weightedSum1 = barDataLarge1.entries.sumOf { (bin, count) ->
        // Trung điểm mỗi bin: bin * 0.5 + 0.25 (binWidth=0.5)
        val binMidpoint = bin * 0.5 + 0.25
        binMidpoint * count
    }
    val avgDiff1 = if (totalCount1 > 0) weightedSum1 / totalCount1 else 0.0

    val fakeDifficultyChartDataLarge1 = DifficultyChartData(
        barData = barDataLarge1,
        avgDiff = avgDiff1
    )

    val barData = fakeDifficultyChartDataLarge1.barData

    runBlocking {
        modelProducer.runTransaction {
            columnSeries {
                series(barData.keys, barData.values)
            }
        }
    }

    KaniTheme {

        Column {

            Text(
                text = barData.toString()
            )

            DifficultyChart(
                modelProducer = modelProducer
            )
        }

    }

}