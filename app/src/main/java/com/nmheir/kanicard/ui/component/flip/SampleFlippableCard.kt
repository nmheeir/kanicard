package com.nmheir.kanicard.ui.component.flip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.domain.ui.model.AppTheme
import com.nmheir.kanicard.core.presentation.components.flip.FlipController
import com.nmheir.kanicard.core.presentation.components.flip.Flippable
import com.nmheir.kanicard.core.presentation.components.flip.rememberFlipController
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.data.dto.CardDto
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.utils.fakeCardList

@Composable
fun SampleFlipCardFrontSide(
    flipController: FlipController,
    card: CardDto
) {
    Box(
        modifier = Modifier
            .aspectRatio((1.5).toFloat())
            .size(CardHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = flipController::flip,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = stringResource(R.string.action_back),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = card.question,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = MaterialTheme.padding.extraSmall)
        )
    }
}

@Composable
fun SampleFlipCardBackSide(
    flipController: FlipController,
    card: CardDto
) {
    Box(
        modifier = Modifier
            .aspectRatio((1.5).toFloat())
            .size(CardHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Button(
            shape = MaterialTheme.shapes.extraLarge,
            onClick = flipController::flip,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = stringResource(R.string.action_front),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = card.answer,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(MaterialTheme.padding.extraSmall)
        )
    }
}

@Preview
@Composable
private fun Test() {
    val flipController = rememberFlipController()
    KaniTheme(
        appTheme = AppTheme.GREEN_APPLE
    ) {
        Flippable(
            frontSide = {
                SampleFlipCardFrontSide(flipController, fakeCardList[1])
            },
            backSide = {
                SampleFlipCardBackSide(flipController, fakeCardList[1])
            },
            flipController = flipController
        )
    }
}

val CardHeight = 156.dp
