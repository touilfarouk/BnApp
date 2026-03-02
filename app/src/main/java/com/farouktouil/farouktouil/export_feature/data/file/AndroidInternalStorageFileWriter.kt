package com.farouktouil.farouktouil.export_feature.data.file
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.farouktouil.farouktouil.core.util.Resource
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

class AndroidInternalStorageFileWriter @Inject constructor(
    private val context: Context
): FileWriter {


    override suspend fun writeFile(byteArray: ByteArray): Resource<String> {
        val dateTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        return saveFile(byteArray, FileWriter.FILE_NAME + "_" + formatter.format(dateTime) + ".csv")
    }

    private fun saveFile(byteArray: ByteArray, fileName: String): Resource<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(byteArray, fileName)
        } else {
            saveToLegacyDownloads(byteArray, fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(byteArray: ByteArray, fileName: String): Resource<String> {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(
                MediaStore.Downloads.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/OrderApp"
            )
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return Resource.Error("Could not create download entry")

        return try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(byteArray)
            } ?: return Resource.Error("Could not open output stream")

            Resource.Success(uri.toString())
        } catch (e: IOException) {
            Resource.Error(errorMessage = e.localizedMessage ?: "unknown error")
        }
    }

    private fun saveToLegacyDownloads(byteArray: ByteArray, fileName: String): Resource<String> {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val folder = File(downloadsDir, "OrderApp")
        if (!folder.exists() && !folder.mkdirs()) {
            return Resource.Error("Could not create Downloads/OrderApp directory")
        }

        val file = File(folder, fileName)
        return try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
            Resource.Success(Uri.fromFile(file).toString())
        } catch (e: IOException) {
            Resource.Error(errorMessage = e.localizedMessage ?: "unknown error")
        }
    }

}
