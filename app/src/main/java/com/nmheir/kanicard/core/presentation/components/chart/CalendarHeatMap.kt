package com.nmheir.kanicard.core.presentation.components.chart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.extensions.isToday

val CalendarItemSize = 20.dp
val RowPadding = 8.dp

// TODO: Rảnh thì làm

@Composable
fun CalendarHeatMap(
    modifier: Modifier = Modifier,
    rowPadding: Dp = RowPadding
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(7)
    ) { }
}

@Composable
private fun CalendarHeatMapItem(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color,
    enabled: Boolean = false,
    borderStroke: BorderStroke = BorderStroke(0.dp, Color.Transparent),
    shape: Shape = MaterialTheme.shapes.extraSmall,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(color)
            .clickable(enabled = enabled, onClick = onClick)
            .border(borderStroke, shape)
    )
}