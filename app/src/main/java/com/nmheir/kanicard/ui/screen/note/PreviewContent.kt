@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.note

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.flip.FlipState
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.ui.component.card.InteractiveFlashcard
import com.nmheir.kanicard.ui.component.webview.RenderHtmlContent
import com.nmheir.kanicard.ui.viewmodels.TemplatePreview

@Composable
fun PreviewContent(
    template: TemplatePreview,
) {
    var reverseLayout by remember { mutableStateOf<FlipState>(FlipState.FRONT) }
    val flipController = rememberFlipController()
    Scaffold(
        bottomBar = {
            Button(
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = {
                    flipController.flip()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(
                        if (reverseLayout == FlipState.FRONT) R.string.action_show_back else R.string.action_show_front
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    ) { pv ->
        InteractiveFlashcard(
            flipController = flipController,
            qHtml = template.qstHtml,
            aHtml = template.ansHtml,
            onFlippedListener = {
                reverseLayout = it
            },
            modifier = Modifier
                .padding(pv)
                .padding(12.dp)
        )
    }
}