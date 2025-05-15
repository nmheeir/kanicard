package com.nmheir.kanicard.ui.component.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.dialog.DefaultDialog

@Composable
fun AlertDialog(
    preventDismissRequest: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    enableButton: () -> Boolean = { true },
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    DefaultDialog(
        preventDismissRequest = preventDismissRequest,
        onDismiss = onDismiss,
        buttons = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            TextButton(
                enabled = enableButton(),
                onClick = {
                    onConfirm()
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        icon = icon,
        title = title,
        content = content
    )
}