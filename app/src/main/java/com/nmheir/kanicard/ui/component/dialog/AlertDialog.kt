package com.nmheir.kanicard.ui.component.dialog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
    confirmText: String = stringResource(R.string.ok),
    dismissText: String = stringResource(R.string.cancel),
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
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = onDismiss
            ) {
                Text(text = dismissText)
            }
            TextButton(
                enabled = enableButton(),
                onClick = {
                    onConfirm()
                }
            ) {
                Text(text = confirmText)
            }
        },
        icon = icon,
        title = title,
        content = content
    )
}