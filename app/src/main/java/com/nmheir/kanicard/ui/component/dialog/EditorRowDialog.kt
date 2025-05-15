package com.nmheir.kanicard.ui.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.theme.KaniTheme
import kotlinx.coroutines.delay
import timber.log.Timber
import java.nio.file.WatchEvent

typealias LinkName = String
typealias LinkUrl = String

@Composable
fun LinkDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (LinkName, LinkUrl) -> Unit
) {
    // TODO: Why haptic doesn't have Confirm
    val haptic = LocalHapticFeedback.current

    var name by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    val linkError by remember {
        derivedStateOf {
            if (link.isNotEmpty()) {
                // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
                !android.util.Patterns.WEB_URL.matcher(link).matches()
            } else {
                false
            }
        }
    }

    DefaultDialog(
        modifier = modifier
            .fillMaxWidth(),
        onDismiss = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
            TextButton(
                enabled = !linkError && name.isNotEmpty() && link.isNotEmpty(),
                onClick = {
                    if (!linkError) {
                        name = name.trim()
                        link = link.trim()
                        if (!link.startsWith("http://") && !link.startsWith("https://")
                            && link.startsWith("www.")
                        ) {
                            link = "https://$link"
                        }
                        onConfirm(name, link)
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        }
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Gap(8.dp)
        OutlinedTextField(
            value = link,
            onValueChange = { link = it },
            singleLine = true,
            isError = linkError,
            label = { Text(text = "URL") },
            placeholder = { Text(text = "https://example.com") },
            supportingText = {
                if (linkError) {
                    Text(text = "Incorrect link format")
                }
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        LinkDialog(onDismiss = {}, onConfirm = { _, _ -> })
    }
}