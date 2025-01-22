package com.nmheir.kanicard.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    label: @Composable () -> Unit,
    placeHolder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        modifier = modifier,
        value = query,
        onValueChange = onQueryChange,
        label = label,
        placeholder = {
            if (placeHolder != null) {
                placeHolder()
            }
        },
        leadingIcon = {
            if (leadingIcon != null) {
                leadingIcon()
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                trailingIcon()
            }
        },
        shape = MaterialTheme.shapes.medium,
        visualTransformation = visualTransformation
    )
}