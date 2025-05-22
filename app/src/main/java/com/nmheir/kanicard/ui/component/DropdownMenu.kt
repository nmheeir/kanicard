package com.nmheir.kanicard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.screen.deck.fakeDeckOptionUsages
import com.nmheir.kanicard.ui.theme.KaniTheme

@Composable
fun <T> ImprovedDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    itemSubLabel: @Composable (item: T?) -> String = { "" }
) {
    var expanded by remember { mutableStateOf(false) }
    var menuWidth by remember { mutableIntStateOf(0) }

    val highlightColor = MaterialTheme.colorScheme.primary
    val selectedItemBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    val defaultTextColor = MaterialTheme.colorScheme.onBackground
    val unselectedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
    val defaultBackgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box {
            // Trigger
            Card(
                modifier = Modifier
                    .clickable { expanded = true }
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        menuWidth = it.size.width
                    },
                shape = if (!expanded) {
                    MaterialTheme.shapes.medium
                } else {
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = defaultBackgroundColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedItem?.let { itemLabel(it) } ?: "Select one preset",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedItem == null) Color.Gray else Color.DarkGray
                        )
                        Text(
                            text = itemSubLabel(selectedItem),
                            style = MaterialTheme.typography.bodyMedium,
                            color = defaultTextColor
                        )
                    }
                    Icon(
                        painter = painterResource(
                            if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down,
                        ),
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = defaultTextColor
                    )
                }
            }

            // Dropdown menu
            DropdownMenu(
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { menuWidth.toDp() })
                    .background(defaultBackgroundColor)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .border(
                        width = 1.dp,
                        color = unselectedIndicatorColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                ,
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = item == selectedItem

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemLabel(item),
                                color = if (isSelected) highlightColor else Color.DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) selectedItemBackgroundColor else unselectedIndicatorColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = if (isSelected) highlightColor else Color.DarkGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = highlightColor
                                )
                            }
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) selectedItemBackgroundColor.copy(alpha = 0.3f) else defaultBackgroundColor)
                    )

                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = unselectedIndicatorColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun abc() {
    KaniTheme {
        ImprovedDropdownMenu(
            modifier = Modifier.padding(top = 64.dp),
            items = fakeDeckOptionUsages,
            selectedItem = fakeDeckOptionUsages[0],
            onItemSelected = {},
            itemLabel = {
                it.option.name
            },
            itemSubLabel = {
                if (it == null) {
                    "No used"
                } else {
                    pluralStringResource(
                        R.plurals.deck_used_by,
                        it.usage.toInt(),
                        it.usage.toInt()
                    )
                }
            }
        )
    }
}