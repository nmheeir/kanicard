package com.nmheir.kanicard.ui.screen.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmheir.kanicard.R
import com.nmheir.kanicard.constants.StoragePathKey
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.core.presentation.components.padding
import com.nmheir.kanicard.extensions.getOrCreateDirectory
import com.nmheir.kanicard.extensions.toast
import com.nmheir.kanicard.utils.rememberPreference

class StorageStep : OnboardingStep {

    private var _isComplete by mutableStateOf(false)

    override val isComplete: Boolean
        get() = _isComplete

    @Composable
    override fun Content() {
        val context = LocalContext.current

        val (storagePath, onStoragePathChange) = rememberPreference(
            StoragePathKey,
            stringResource(R.string.no_location_set)
        )

        _isComplete = storagePath != stringResource(R.string.no_location_set)

        val folderPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri: Uri? ->
            uri?.let {
                context.applicationContext.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val kaniCardDir =
                    getOrCreateDirectory(context, it, Constants.File.KANI_CARD)

                kaniCardDir?.let { dir ->
                    getOrCreateDirectory(dir, Constants.File.KANI_CARD_IMAGE)
                    getOrCreateDirectory(dir, Constants.File.KANI_CARD_AUDIO)
                    getOrCreateDirectory(dir, Constants.File.KANI_CARD_VIDEO)
                    getOrCreateDirectory(dir, Constants.File.KANI_CARD_BACKUP)
                }

                onStoragePathChange(it.toString())
            }
        }


        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium)
        ) {
            Text(
                text = stringResource(
                    R.string.onboarding_storage_info,
                    stringResource(R.string.app_name),
                    storagePath
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    try {
                        folderPicker.launch(getDocumentDirectoryUri())
                    } catch (e: Exception) {
                        context.toast(R.string.file_picker_error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.onboarding_storage_action_select)
                )
            }
        }
    }
}

/**
 * Helper function to get the URI for the Documents directory.
 */
private fun getDocumentDirectoryUri(): Uri? {
    val documentsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    return if (documentsDir != null) {
        Uri.fromFile(documentsDir)
    } else {
        null
    }
}