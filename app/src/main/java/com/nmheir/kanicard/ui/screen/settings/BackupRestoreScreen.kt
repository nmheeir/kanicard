@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.screen.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import com.nmheir.kanicard.ui.viewmodels.BackUpRestoreViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.ui.component.Gap
import com.nmheir.kanicard.ui.component.dialog.DefaultDialog

@Composable
fun BackupRestoreScreen(
    navController: NavHostController,
    viewModel: BackUpRestoreViewModel = hiltViewModel()
) {
    var showCsvDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Backup and Restore") },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp
                    ) {
                        Icon(painterResource(R.drawable.ic_arrow_back_ios), null)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { pv ->
        LazyColumn(
            contentPadding = pv
        ) {
            items(
                items = items
            ) {
                TextPreferenceWidget(
                    title = stringResource(it.titleRes),
                    icon = it.iconRes,
                    subtitle = stringResource(it.subTitleRes),
                    onPreferenceClick = {
                        when (it.titleRes) {
                            R.string.backup -> {

                            }

                            R.string.restore -> {

                            }

                            R.string.csv_import_export -> {
                                showCsvDialog = true
                            }
                        }
                    }
                )
            }
        }
    }
    if (showCsvDialog) {
        DefaultDialog(
            onDismiss = { showCsvDialog = false }
        ) {
            OutlinedButton(
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(12.dp),
                onClick = {}
            ) {
                Icon(painterResource(R.drawable.ic_download), null)
                Text(
                    text = stringResource(R.string.label_import_csv),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }

            Gap(12.dp)

            OutlinedButton(
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(12.dp),
                onClick = {}
            ) {
                Icon(painterResource(R.drawable.ic_upload_file), null)
                Text(
                    text = stringResource(R.string.label_export_csv),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class BackUpRestoreItem(
    @StringRes val titleRes: Int,
    @StringRes val subTitleRes: Int,
    @DrawableRes val iconRes: Int,
)

private val items = listOf(
    BackUpRestoreItem(
        titleRes = R.string.backup,
        subTitleRes = R.string.sub_title_backup,
        iconRes = R.drawable.ic_cloud_upload,
    ),
    BackUpRestoreItem(
        titleRes = R.string.restore,
        subTitleRes = R.string.backup_restore,
        iconRes = R.drawable.ic_settings_backup_restore,
    ),
    BackUpRestoreItem(
        titleRes = R.string.csv_import_export,
        subTitleRes = R.string.sub_title_csv_import_export,
        iconRes = R.drawable.ic_csv
    )
)