package com.nmheir.kanicard.ui.component.dialog

import android.app.ProgressDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.theme.KaniTheme

@Composable
fun ProgressDialog(
    modifier: Modifier = Modifier,
    currentProgress: () -> Float,
    title: String
) {
    val progressText by remember(currentProgress()) {
        derivedStateOf {
            (currentProgress() * 100).toString() + " %"
        }
    }

    DefaultDialog(
        onDismiss = {},
        preventDismissRequest = true,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = progressText,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Gap(12.dp)
        LinearProgressIndicator(
            progress = currentProgress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Test() {
    KaniTheme {
        ProgressDialog(
            currentProgress = {
                0.12f
            },
            title = "Loading"
        )
    }
}