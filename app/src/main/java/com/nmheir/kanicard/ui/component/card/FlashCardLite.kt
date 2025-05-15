package com.nmheir.kanicard.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.flip.FlipController
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.flip.CardHeight
import com.nmheir.kanicard.ui.screen.note.CardSide
import com.nmheir.kanicard.ui.theme.KaniTheme

@Composable
fun FlashCardLite(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    side: CardSide,
) {
    Box(
        modifier = modifier
            .aspectRatio((1.7).toFloat())
            .size(CardHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = onClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = stringResource(side.title),
                style = MaterialTheme.typography.bodySmall
            )
            Gap(4.dp)
            Icon(
                painterResource(
                    if (side == CardSide.Front) R.drawable.ic_flip_to_back else R.drawable.ic_flip_to_front
                ),
                null
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = MaterialTheme.padding.extraSmall)
        )
    }
}

private val CardHeight = 156.dp

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        val flipController = rememberFlipController()
        FlashCardLite(
            text = "Con cac",
            side = CardSide.Front,
            modifier = Modifier,
            onClick = {}
        )
    }
}