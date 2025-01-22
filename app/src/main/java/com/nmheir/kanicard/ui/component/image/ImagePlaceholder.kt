package com.nmheir.kanicard.ui.component.image

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor

@Composable
fun imagePlaceholderDefaultBrush(): Brush {
    return SolidColor(MaterialTheme.colorScheme.surfaceVariant)
}