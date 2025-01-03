package pt.isec.ans.amovcontacts.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileUtils {
    companion object {
        fun getTempFilename(
            context: Context,
            prefix : String = "image",
            suffix : String = ".img"
        ) : String = File.createTempFile(
            prefix, suffix,
            context.externalCacheDir
        ).absolutePath

        fun getInternalFilename(
            context: Context,
            prefix : String = "image",
            suffix : String = ".img"
        ) : String = File.createTempFile(
            prefix, suffix,
            context.filesDir
        ).absolutePath

        fun createFileFromUri(
            context: Context,
            uri : Uri,
            filename : String = getInternalFilename(context)
        ) : String {
            FileOutputStream(filename).use { outputStream ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return filename
        }
        fun copyFile(
            originalPath : String,
            newFilename : String
        ) : String {
            FileOutputStream(newFilename).use { outputStream ->
                FileInputStream(originalPath).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return newFilename
        }
        fun copyFile(
            context: Context,
            originalPath : String,
            newFilename : String = getInternalFilename(context)
        ) : String {
            FileOutputStream(newFilename).use { outputStream ->
                FileInputStream(originalPath).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return newFilename
        }
    }
}