package com.nmheir.kanicard.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastForEach

@Composable
fun <T> IconButtonDropdownMenu(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null,
    values: List<T>,
    valueText: @Composable (T) -> String,
    onValueSelected: (T) -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { showDropdown = true }
        ) {
            Icon(painterResource(iconRes), contentDescription)
        }
        if (showDropdown) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { showDropdown = false }
            ) {
                values.fastForEach { value ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = valueText(value),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        onClick = {
                            showDropdown = false
                            onValueSelected(value)
                        }
                    )
                }
            }
        }
    }
}