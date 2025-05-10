package com.nmheir.kanicard.extensions

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

operator fun File.div(child: String): File = File(this, child)

fun InputStream.zipInputStream(): ZipInputStream = ZipInputStream(this)
fun OutputStream.zipOutputStream(): ZipOutputStream = ZipOutputStream(this)

fun getOrCreateDirectory(
    context: Context, parentUri: Uri, dirName: String
): DocumentFile? {
    val parent = DocumentFile.fromTreeUri(context, parentUri)
        ?: return null // If can't get parent folder, return null

    return try {
        parent.findFile(dirName)?.let { existingFile ->
            if (existingFile.isDirectory) {
                // If a directory with the same name already exists, it will be returned directly.
                return existingFile
            }
        }

        parent.createDirectory(dirName)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getOrCreateDirectory(
    parent: DocumentFile,
    dirName: String
): DocumentFile? {
    parent.findFile(dirName)?.let {
        if (it.isDirectory) return it
    }
    return parent.createDirectory(dirName)
}
