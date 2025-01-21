package com.example.kanicard.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Size {
    val none = 0.dp
    val extraExtraTiny = 1.dp
    val extraTiny = 2.dp
    val tiny = 4.dp
    val small = 8.dp
    val smedium = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
    val extraHuge = 56.dp
    val navBarSize = 68.dp
}

val NavigationBarAnimationSpec = spring<Dp>(stiffness = Spring.StiffnessMedium)
