package com.nmheir.kanicard.ui.screen.statistics.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.ui.screen.statistics.model.DifficultyChartData
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
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

    Column {
        Text(
            text = data.barData.toString()
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
    shiftExtremeLines = true,
    addExtremeLabelPadding = false
)

@Composable
private fun DifficultyChart(
    modelProducer: CartesianChartModelProducer
) {

    val color = MaterialTheme.colorScheme.secondaryContainer

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(color),
                        thickness = 16.dp
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.fixed()
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                itemPlacer = BottomAxisItemPlacer,
            )
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
            put(bin, Random.nextInt(99) + 10)
        }
        put(95, 100)
        put(100, 100)
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