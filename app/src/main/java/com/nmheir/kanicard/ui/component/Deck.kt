package com.nmheir.kanicard.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.data.dto.deck.DeckWidgetData
import com.nmheir.kanicard.extensions.relativeTime
import com.nmheir.kanicard.ui.theme.KaniTheme
import com.nmheir.kanicard.utils.fakeDeckWidgetData

@Composable
fun DeckItem(
    modifier: Modifier = Modifier,
    deck: DeckWidgetData,
    onLearn: () -> Unit,
    onOption: () -> Unit,
    onEdit: () -> Unit,
    onView: () -> Unit
) {
    var showDetail by rememberSaveable { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        onClick = onLearn,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .animateContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            modifier = Modifier.padding(MaterialTheme.padding.small)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = deck.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall)
                    ) {
                        Icon(painterResource(R.drawable.ic_schedule), null)
                        Text(
                            text = if (deck.lastReview == null) {
                                stringResource(R.string.info_no_review)
                            } else {
                                """${stringResource(R.string.info_last_review)} ${deck.lastReview.relativeTime()}"""
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                IconButton(
                    onClick = { showDetail = !showDetail }
                ) {
                    Icon(
                        painterResource(if (!showDetail) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up),
                        null
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatBadge(
                    count = deck.newCount,
                    label = stringResource(R.string.label_new),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
                StatBadge(
                    count = deck.reviewCount,
                    label = stringResource(R.string.label_reviewing),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                StatBadge(
                    count = deck.learnCount,
                    label = stringResource(R.string.label_learning),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            if (!showDetail) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                    horizontalAlignment = Alignment.Start,
                ) {
                    HorizontalDivider()
                    StatisticRow(
                        label = stringResource(R.string.label_due_today),
                        count = deck.dueToday
                    )
                    HorizontalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.weight(3f)
                        ) {
                            DeckFunction(
                                label = stringResource(R.string.action_view),
                                iconRes = R.drawable.ic_visibility,
                                onClick = onView,
                                modifier = Modifier.weight(1f)
                            )
                            DeckFunction(
                                label = stringResource(R.string.action_edit),
                                iconRes = R.drawable.ic_edit,
                                onClick = onEdit,
                                modifier = Modifier.weight(1f)
                            )
                            DeckFunction(
                                label = stringResource(R.string.action_options),
                                iconRes = R.drawable.ic_tune,
                                onClick = onOption,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        TextButton(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = MaterialTheme.shapes.small,
                            onClick = onLearn,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(R.string.action_learn))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckFunction(
    modifier: Modifier = Modifier,
    label: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(painterResource(iconRes), null)
        Gap(MaterialTheme.padding.extraSmall)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
//        modifier = modifier
//            .clickable { onClick() }
//    ) {
//
//    }
}

@Composable
private fun StatisticRow(
    modifier: Modifier = Modifier,
    label: String,
    count: Int
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.labelMedium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = count.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cards",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatBadge(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    containerColor: Color,
    contentColor: Color
) {
    Badge(
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Text(
            text = "$count $label",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(MaterialTheme.padding.extraSmall)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        DeckItem(
            deck = fakeDeckWidgetData[0],
            onLearn = {},
            onOption = {},
            onEdit = {},
            onView = {}
        )
    }
}