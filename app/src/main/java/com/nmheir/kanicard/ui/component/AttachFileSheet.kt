@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmheir.kanicard.ui.component

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.nmheir.kanicard.R
import com.nmheir.kanicard.ui.component.dialog.AlertDialog
import com.nmheir.kanicard.ui.component.widget.TextPreferenceWidget
import timber.log.Timber

@Composable
fun AttachFileSheet(
    modifier: Modifier = Modifier,
    onAudioClipSelection: (Uri) -> Unit,
    onGallerySelection: (Uri) -> Unit,
    onVideoClipSelection: (Uri) -> Unit,
    onRecordAudioSelection: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Timber.d("Selected image uri: $uri")
            onGallerySelection(uri)
            onDismiss()
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Timber.d("Selected video uri: $uri")
            onVideoClipSelection(uri)
            onDismiss()
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            Timber.d("Selected audio uri: $it")
            onAudioClipSelection(it)
            onDismiss()
        }
    }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            Timber.d("Selected audio uri: $uri")
            onRecordAudioSelection(uri)
            onDismiss()
        }
    }

    var notFoundRecordAudioDialog by remember { mutableStateOf(false) }
    if (notFoundRecordAudioDialog) {
        AlertDialog(
            onDismiss = { notFoundRecordAudioDialog = false },
            onConfirm = { notFoundRecordAudioDialog = false }
        ) {
            Text(text = "Record audio not found")
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AttachFileSheetOption.options.fastForEach { option ->
                TextPreferenceWidget(
                    title = option.title,
                    icon = option.iconRes,
                    onPreferenceClick = {
                        when (option) {
                            AttachFileSheetOption.AudioClip -> {
                                audioPicker.launch(
                                    "audio/*"
                                )
                            }

                            AttachFileSheetOption.Gallery -> {
                                imagePicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }

                            AttachFileSheetOption.RecordAudio -> {
                                val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
                                try {
                                    recordAudioLauncher.launch(intent)
                                } catch (e: Exception) {
                                    notFoundRecordAudioDialog = true
                                    e.printStackTrace()
                                }
                            }

                            AttachFileSheetOption.VideoClip -> {
                                videoPicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.VideoOnly
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

private sealed class AttachFileSheetOption(
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object RecordAudio : AttachFileSheetOption("Record Audio", R.drawable.ic_record_voice_over)
    object Gallery : AttachFileSheetOption("Gallery", R.drawable.ic_image_fill)
    object AudioClip : AttachFileSheetOption("Add Audio Clip", R.drawable.ic_music_note)
    object VideoClip : AttachFileSheetOption("Add Video Clip", R.drawable.ic_video_library)

    companion object {
        val options = listOf(RecordAudio, Gallery, AudioClip, VideoClip)
    }
}