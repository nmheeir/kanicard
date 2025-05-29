package com.nmheir.kanicard.ui.component.card

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.core.presentation.components.flip.FlipController
import com.nmheir.kanicard.core.presentation.components.flip.FlipState
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.ui.component.webview.RenderHtmlContent

@Composable
fun InteractiveFlashcard(
    modifier: Modifier = Modifier,
    flipController: FlipController,
    qHtml: String,
    aHtml: String,
    enableFlip: Boolean = true,
    onFlippedListener: (currentSide: FlipState) -> Unit = { _ -> }
) {
    val current by rememberUpdatedState(enableFlip)

    Box(
        modifier = modifier
    ) {
        Flippable(
            contentAlignment = Alignment.Center,
            frontSide = {
                RenderHtmlContent(
                    html = qHtml,
                    onWebViewClick = {
                        if (current)
                            flipController.flipToBack()
                    },
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
                )
            },
            backSide = {
                RenderHtmlContent(
                    html = aHtml,
                    onWebViewClick = {
                        if (current)
                            flipController.flipToFront()
                    },
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
                )
            },
            flipOnTouch = false,
            flipController = flipController,
            onFlippedListener = onFlippedListener
        )
    }
}