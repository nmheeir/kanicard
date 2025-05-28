package com.nmheir.kanicard.ui.screen.statistics.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.core.presentation.utils.extractMembers
import com.nmheir.kanicard.ui.screen.statistics.model.CardCountChartData
import com.nmheir.kanicard.ui.theme.KaniTheme
import hu.ma.charts.legend.data.LegendEntry
import hu.ma.charts.legend.data.LegendPosition
import hu.ma.charts.pie.PieChart
import hu.ma.charts.pie.data.PieChartData
import hu.ma.charts.pie.data.PieChartEntry

@Composable
fun CardCountChart(
    modifier: Modifier = Modifier,
    data: CardCountChartData
) {
    val pieColors = listOf(
        Color(0xFF5B9BD5), // Xanh dương nhạt
        Color(0xFFFF8C42), // Cam
        Color(0xFFFF6B6B), // Đỏ cam/Coral
        Color(0xFF90EE90), // Xanh lá nhạt
        Color(0xFF4CAF50), // Xanh lá đậm
    )

    val total = remember(data) {
        data.total
    }

    val pieChartEntries = remember(data) {
        extractMembers(data)
            .drop(1)
            .mapIndexed { index, (label, valueStr) ->
                val value = valueStr.toFloatOrNull() ?: 10f
                PieChartEntry(
                    value = value,
                    label = AnnotatedString(label.replaceFirstChar { it.uppercase() }),
                    color = pieColors.getOrNull(index)
                )
            }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(400.dp)
            .padding(12.dp)
    ) {
        PieChart(
            data =
                PieChartData(
                    entries = pieChartEntries,
                    legendPosition = LegendPosition.Bottom
                ),
            chartSize = 160.dp,
            sliceWidth = 0.dp,
            sliceSpacing = 0.dp,
            useMinimumSliceAngle = false
        ) { legends ->
            CardCountLegend(
                total = total,
                legends = legends,
                modifier = Modifier.height(240.dp)
            )
        }
    }
}

@Composable
private fun CardCountLegend(
    modifier: Modifier = Modifier,
    total: Int,
    legends: List<LegendEntry>
) {
    val state = rememberLazyListState()

    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        items(
            items = legends
        ) { legend ->
            CardCountLegendItem(
                cardTypeName = legend.text.text.replaceFirstChar { it.uppercase() },
                total = legend.value?.toInt() ?: 0,
                percent = legend.percent,
                isSelected = false,
                color = legend.shape.color,
                onClick = { }
            )
        }

        item {
            CardCountLegendItem(
                cardTypeName = "Total",
                total = total,
                percent = 100f,
                isSelected = false,
                color = Color.Transparent,
                onClick = { }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CardCountLegendItem(
    modifier: Modifier = Modifier,
    cardTypeName: String,
    total: Int,
    percent: Float,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        color = when (isSelected) {
            true -> MaterialTheme.colorScheme.primaryContainer
            false -> Color.Transparent
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )

                Text(
                    text = cardTypeName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(2f)
            ) {

                Text(
                    text = total.toString(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = String.format("%.1f%%", percent),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Test() {
    KaniTheme {
        val fakeData = CardCountChartData(
            total = 200,
            new = 50,
            learning = 40,
            relearning = 30,
            young = 40,
            mature = 40
        )

        val fakeData1 = CardCountChartData(
            new = 17382,
            learning = 24671,
            relearning = 8913,
            young = 19436,
            mature = 29698
        )



        CardCountChart(
            data = fakeData1
        )
    }
}