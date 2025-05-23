package com.nmheir.kanicard.core.presentation.components.chart.model

import androidx.compose.ui.graphics.Color
import com.nmheir.kanicard.core.presentation.components.chart.common.ChartColor
import com.nmheir.kanicard.core.presentation.components.chart.common.asSolidChartColor

/**
 * Data class representing a single slice of a pie chart.
 *
 * @property value The value of the pie chart slice.
 * @property color The color of the pie chart slice.
 * @property labelColor The color of the label text for the pie chart slice. Defaults to white.
 * @property label The text label for the pie chart slice.
 */
data class PieChartData(
    val value: Float,
    val color: ChartColor,
    val labelColor: ChartColor = Color.White.asSolidChartColor(),
    val label: String,
)