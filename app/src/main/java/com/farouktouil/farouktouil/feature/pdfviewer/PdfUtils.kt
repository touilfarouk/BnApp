package com.farouktouil.farouktouil.feature.pdfviewer

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
fun rememberPdfFileFromUrl(url: String): File? {
    val context = LocalContext.current
    return remember(url) {
        try {
            val fileName = url.substringAfterLast('/')
            val file = File(context.cacheDir, fileName)
            if (!file.exists()) {
                context.assets.open(url).use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun getPdfFileFromUrl(context: Context, url: String): File? {
    return try {
        val fileName = url.substringAfterLast('/')
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) {
            context.assets.open(url).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
