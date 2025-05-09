package com.nmheir.kanicard.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nmheir.kanicard.R
import com.nmheir.kanicard.core.presentation.utils.hozPadding
import kotlinx.coroutines.delay

@Composable
fun DefaultDialog(
    modifier: Modifier = Modifier,
    preventDismissRequest: Boolean = false,
    onDismiss: () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    buttons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = if (preventDismissRequest) ({}) else onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.padding(24.dp),
            shape = MaterialTheme.shapes.small,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .padding(24.dp)
            ) {
                if (icon != null) {
                    CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.iconContentColor) {
                        Box(
                            Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            icon()
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
                if (title != null) {
                    CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.titleContentColor) {
                        ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                            Box(
                                // Align the title to the center when an icon is present.
                                Modifier.align(if (icon == null) Alignment.Start else Alignment.CenterHorizontally)
                            ) {
                                title()
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                content()

                if (buttons != null) {
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.labelLarge
                            ) {
                                buttons()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.padding(24.dp),
            shape = MaterialTheme.shapes.small,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(vertical = 24.dp)
            ) {
                LazyColumn(content = content)
            }
        }
    }
}

@Composable
fun <T> ListOptionDialog(
    modifier: Modifier = Modifier,
    selectedValue: T,
    values: List<T>,
    valueText: @Composable (T) -> String,
    onValueSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    ListDialog(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (values.isEmpty()) {
            item {
                Text(
                    text = "No options",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .hozPadding()
                )
            }
        }
        items(values) { value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onValueSelected(value)
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RadioButton(
                    selected = value == selectedValue,
                    onClick = null
                )

                Text(
                    text = valueText(value),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun TextFieldDialog(
    modifier: Modifier = Modifier,
    preventDismissRequest: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    initialTextFieldValue: TextFieldValue = TextFieldValue(),
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 10,
    isInputValid: (String) -> Boolean = { it.isNotEmpty() },
    onDone: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val (textFieldValue, onTextFieldValueChange) = remember {
        mutableStateOf(initialTextFieldValue)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    DefaultDialog(
        preventDismissRequest = preventDismissRequest,
        onDismiss = onDismiss,
        modifier = modifier,
        icon = icon,
        title = title,
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }

            TextButton(
                enabled = isInputValid(textFieldValue.text),
                onClick = {
                    onDismiss()
                    onDone(textFieldValue.text)
                }
            ) {
                Text(text = stringResource(android.R.string.ok))
            }
        }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            placeholder = placeholder,
            singleLine = singleLine,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(),
            keyboardOptions = KeyboardOptions(imeAction = if (singleLine) ImeAction.Done else ImeAction.None),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone(textFieldValue.text)
                    onDismiss()
                }
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = false)
                .focusRequester(focusRequester)
        )
    }
}

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