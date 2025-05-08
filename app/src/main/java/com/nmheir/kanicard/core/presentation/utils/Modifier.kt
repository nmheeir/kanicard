package com.nmheir.kanicard.core.presentation.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.nmheir.kanicard.core.presentation.components.SECONDARY_ALPHA
import com.nmheir.kanicard.core.presentation.components.padding

fun Modifier.secondaryItemAlpha(): Modifier = this.alpha(SECONDARY_ALPHA)

fun Modifier.hozPadding(): Modifier = this.padding(horizontal = MaterialTheme.padding.mediumSmall)
