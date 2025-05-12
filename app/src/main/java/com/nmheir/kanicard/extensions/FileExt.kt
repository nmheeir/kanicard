package com.nmheir.kanicard.extensions

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.nmheir.kanicard.constants.StoragePathKey
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

operator fun File.div(child: String): File = File(this, child)

fun InputStream.zipInputStream(): ZipInputStream = ZipInputStream(this)
fun OutputStream.zipOutputStream(): ZipOutputStream = ZipOutputStream(this)

fun hasFileWithName(dir: DocumentFile, fileName: String): Boolean {
    return dir.listFiles().any { file ->
        file.name == fileName
    }
}

// Checks whether a file with a specified name exists in the directory
fun getFileName(context: Context, uri: Uri): String? {
    val docFile = DocumentFile.fromSingleUri(context, uri)
    return docFile?.name
}

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

fun saveAudioToDirectory(
    context: Context,
) {
    val path = context.dataStore[StoragePathKey]
}

// Get the extension using the DocumentFile API
fun getFileExtension(context: Context, uri: Uri): String {
    val sourceFile = DocumentFile.fromSingleUri(context, uri)

    // Try getting the extension from the filename
    sourceFile?.name?.let { name ->
        val lastDot = name.lastIndexOf('.')
        if (lastDot > 0) {
            return name.substring(lastDot + 1)
        }
    }

    // If the extension is not available from the file name, it is inferred from the MIME type.
    val mimeType = sourceFile?.type ?: context.contentResolver.getType(uri)
    return when (mimeType) {
        "audio/mpeg" -> "mp3"
        "audio/wav", "audio/x-wav" -> "wav"
        "audio/ogg" -> "ogg"
        "audio/aac" -> "aac"
        "audio/x-m4a" -> "m4a"
        else -> "mp3"  // 默认扩展名
    }
}

fun convertFileName(context: Context, uri: Uri) : String {
    val timestamp = System.currentTimeMillis()
    val name = getFileName(context, uri)
    val fileName = "${name?.substringBeforeLast(".")}_${timestamp}.${
        name?.substringAfterLast(".")
    }"
    return fileName
}