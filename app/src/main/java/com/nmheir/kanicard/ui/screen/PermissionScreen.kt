package com.nmheir.kanicard.ui.screen

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.nmheir.kanicard.core.presentation.components.Constants
import com.nmheir.kanicard.extensions.getOrCreateDirectory
import timber.log.Timber

@Composable
fun PermissionScreen(
    onPermissionResult: (String) -> Unit
) {
    val context = LocalContext.current

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
            onPermissionResult(uri.toString())
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Select root directory location of the file repository"
        )
        TextButton(
            onClick = {
                folderPicker.launch(getDocumentDirectoryUri())
            }
        ) {
            Text(
                text = "Select Folder"
            )
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