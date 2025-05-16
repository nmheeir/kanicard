package com.nmheir.kanicard.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog

@Composable
fun IconButtonDialog(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null,
    dialogContent: @Composable () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    IconButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Icon(painterResource(iconRes), contentDescription)
    }
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            dialogContent()
        }
    }
}