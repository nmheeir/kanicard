package com.nmheir.kanicard.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nmheir.kanicard.core.presentation.components.flip.FlipController
import com.nmheir.kanicard.core.presentation.components.flip.FlipState
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.swipe.swipeableCard
import com.nmheir.kanicard.ui.component.webview.RenderHtmlContent

@Composable
fun InteractiveFlashcard(
    modifier: Modifier = Modifier,
    flipController: FlipController,
    qHtml: String,
    aHtml: String,
    onFlippedListener: (currentSide: FlipState) -> Unit = { _ -> }
) {
    Box(
        modifier = modifier
    ) {
        Flippable(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .zIndex(-1f),
            frontSide = {
                RenderHtmlContent(
                    html = qHtml,
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
                )
            },
            backSide = {
                RenderHtmlContent(
                    html = aHtml,
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
                )
            },
            flipOnTouch = false,
            flipController = flipController,
            onFlippedListener = onFlippedListener
        )

        Box(
            modifier = Modifier
                .zIndex(1f)
                .background(Color.Transparent)
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    flipController.flip()
                }
        )
    }
}