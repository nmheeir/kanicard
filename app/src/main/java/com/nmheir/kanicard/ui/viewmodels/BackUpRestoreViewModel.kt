package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.nmheir.kanicard.data.local.InternalDatabase
import com.nmheir.kanicard.data.local.KaniDatabase
import com.nmheir.kanicard.extensions.tryOrNull
import com.nmheir.kanicard.extensions.zipInputStream
import com.nmheir.kanicard.extensions.zipOutputStream
import com.nmheir.kanicard.ui.activities.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class BackUpRestoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: KaniDatabase
) : ViewModel() {

    fun backup(context: Context, uri: Uri) {
        runCatching {
            context.applicationContext.contentResolver.openOutputStream(uri)?.use {
                it.buffered().zipOutputStream().use { outputStream ->
                    /*(context.filesDir / "datastore" / SETTINGS_FILENAME).inputStream().buffered().use { inputStream ->
                        outputStream.putNextEntry(ZipEntry(SETTINGS_FILENAME))
                        inputStream.copyTo(outputStream)
                    }*/
                    runBlocking(Dispatchers.IO) {
                        database.checkpoint()
                    }
                    FileInputStream(database.openHelper.writableDatabase.path).use { inputStream ->
                        outputStream.putNextEntry(ZipEntry(InternalDatabase.DB_NAME))
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }.onSuccess {
            Timber.d("Backup success: $it")
        }.onFailure {
//            reportException(it)
            Timber.e(it, "Backup failed")
        }
    }

    fun restore(uri: Uri) {
        runCatching {
            context.applicationContext.contentResolver.openInputStream(uri)?.use {
                it.zipInputStream().use { inputStream ->
                    var entry = tryOrNull { inputStream.nextEntry } // prevent ZipException
                    while (entry != null) {
                        when (entry.name) {
                            /*
                                                        SETTINGS_FILENAME -> {
                                                            (context.filesDir / "datastore" / SETTINGS_FILENAME).outputStream().use { outputStream ->
                                                                inputStream.copyTo(outputStream)
                                                            }
                                                        }
                            */

                            InternalDatabase.DB_NAME -> {
                                runBlocking(Dispatchers.IO) {
                                    database.checkpoint()
                                }
                                database.close()
                                FileOutputStream(database.openHelper.writableDatabase.path).use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                        }
                        entry = tryOrNull { inputStream.nextEntry } // prevent ZipException
                    }
                }
            }
            context.startActivity(Intent(context, MainActivity::class.java))
            exitProcess(0)
        }.onSuccess {
            Timber.d("Restore success")
        }.onFailure {
            Timber.e(it, "Restore failed")
        }
    }
}