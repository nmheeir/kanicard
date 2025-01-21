package com.example.kanicard.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kanicard.R
import com.example.kanicard.data.entities.DeckEntity
import com.example.kanicard.ui.component.image.CoilImage
import com.example.kanicard.utils.fakeDeck


@Composable
fun DeckList(modifier: Modifier = Modifier) {

}

@Composable
fun DeckItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    deck: DeckEntity
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
        ) {
            CoilImage(
                imageUrl = deck.thumbnail,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Gap(width = 8.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }
                    ) {
                        Icon(
                            painter = if (expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                                R.drawable.ic_arrow_down
                            ),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        if (expanded) {
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due Today",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "220",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Cards"
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Studied Today",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "73",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Cards"
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New cards",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "123",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Cards"
                    )
                }
            }
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Row {
                DeckOption(
                    onClick = {},
                    option = "View",
                    icon = R.drawable.ic_visibility
                )

                DeckOption(
                    onClick = {},
                    option = "Edit",
                    icon = R.drawable.ic_edit
                )

                DeckOption(
                    onClick = {

                    },
                    option = "Options",
                    icon = R.drawable.ic_tune
                )

                TextButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {

                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.inversePrimary,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.learn),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }


    }
}

@Composable
private fun DeckOption(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    option: String
) {
    Row(
        modifier = modifier
            .clickable {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null
        )
        Text(
            text = option,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

enum class DeckOption {
    VIEW,
    EDIT,
    OPTIONS
}

@Preview(showBackground = true)
@Composable
private fun TestCardItem() {
    DeckItem(
        onClick = {},
        deck = fakeDeck
    )
}