@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.core.presentation

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun IconButtonTooltip(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shortCutDescription: String? = null,
    @DrawableRes iconRes: Int,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            if (shortCutDescription != null) {
                PlainTooltip {
                    Text(
                        text = shortCutDescription,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        state = rememberTooltipState(),
        focusable = false,
        modifier = modifier
    ) {
        IconButton(
            enabled = enabled,
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}

@Composable
fun IconTooltip(
    modifier: Modifier = Modifier,
    shortCutDescription: String? = null,
    @DrawableRes iconRes: Int,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            if (shortCutDescription != null) {
                PlainTooltip {
                    Text(
                        text = shortCutDescription,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        state = rememberTooltipState(),
        focusable = false,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint
        )
    }
}