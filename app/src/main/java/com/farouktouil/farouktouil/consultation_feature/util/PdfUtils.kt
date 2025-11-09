package com.farouktouil.farouktouil.consultation_feature.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.LruCache
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request




import java.util.concurrent.TimeUnit

private const val TAG = "PdfUtils"

object PdfUtils {
    private const val CACHE_DIR = "pdf_thumbnails"
    private const val CACHE_SIZE = 10 * 1024 * 1024 // 10MB cache
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val diskCache by lazy {
        val cacheDir = File(System.getProperty("java.io.tmpdir"), CACHE_DIR)
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                Log.e(TAG, "Failed to create cache directory")
            }
        }
        cacheDir
    }
    
    private val memoryCache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024 // Size in KB
        }
    }
    
    private fun getCacheKey(pdfUrl: String, width: Int, height: Int): String {
        return "${pdfUrl.hashCode()}_${width}x$height"
    }
    
    private suspend fun downloadPdf(url: String, outputFile: File) = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/pdf")
                .header("User-Agent", "Mozilla/5.0")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Failed to download PDF: ${response.code} ${response.message}")
                }
                
                response.body?.use { body ->
                    val input = body.byteStream()
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                        output.fd.sync()
                    }
                } ?: throw Exception("Empty response body")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading PDF from $url: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun loadPdfThumbnail(context: Context, pdfUrl: String, width: Int, height: Int): Bitmap? {
        if (pdfUrl.isBlank()) {
            Log.w(TAG, "Empty PDF URL provided")
            return null
        }
        
        return withContext(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                // Check memory cache first
                val cacheKey = getCacheKey(pdfUrl, width, height)
                memoryCache.get(cacheKey)?.let { 
                    Log.d(TAG, "Using cached thumbnail for: $pdfUrl")
                    return@withContext it 
                }
                
                Log.d(TAG, "Loading PDF thumbnail from URL: $pdfUrl")
                tempFile = File(diskCache, "temp_${System.currentTimeMillis()}.pdf")
                
                // Ensure parent directories exist
                tempFile?.parentFile?.mkdirs()
                
                // Download PDF to a temporary file
                Log.d(TAG, "Downloading PDF to temporary file: ${tempFile?.absolutePath}")
                downloadPdf(pdfUrl, tempFile!!)
                
                if (tempFile?.exists() != true) {
                    throw IllegalStateException("Downloaded file doesn't exist")
                }
                
                if (tempFile?.length() == 0L) {
                    throw IllegalStateException("Downloaded file is empty")
                }
                
                Log.d(TAG, "PDF downloaded successfully, size: ${tempFile?.length()} bytes")

                // Render first page as bitmap
                val fileDescriptor = ParcelFileDescriptor.open(
                    tempFile!!, 
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
                
                val pdfRenderer = PdfRenderer(fileDescriptor)
                
                try {
                    Log.d(TAG, "Rendering first page to bitmap (${width}x$height)")
                    val page = pdfRenderer.openPage(0) // Get first page
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    
                    // Cache the bitmap
                    memoryCache.put(cacheKey, bitmap)
                    Log.d(TAG, "Successfully rendered and cached PDF thumbnail")
                    
                    page.close()
                    bitmap
                } finally {
                    try {
                        pdfRenderer.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error closing PDF renderer", e)
                    }
                    
                    try {
                        fileDescriptor.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error closing file descriptor", e)
                    }
                    
                    // Clean up temporary file
                    tempFile?.let { file ->
                        if (file.exists() && !file.delete()) {
                            Log.w(TAG, "Failed to delete temporary file: ${file.absolutePath}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading PDF thumbnail: ${e.message}", e)
                null
            }
        }
    }
}

@Composable
fun rememberPdfThumbnail(
    pdfUrl: String,
    width: Int = 200,
    height: Int = 200
): State<Bitmap?> {
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    
    DisposableEffect(pdfUrl) {
        if (pdfUrl.isBlank()) {
            Log.w(TAG, "Empty PDF URL provided")
            return@DisposableEffect onDispose {}
        }
        
        Log.d(TAG, "Starting to load thumbnail for URL: $pdfUrl")
        var job = scope.launch(Dispatchers.IO) {
            try {
                val bitmap = PdfUtils.loadPdfThumbnail(context, pdfUrl, width, height)
                bitmap?.let { 
                    Log.d(TAG, "Thumbnail loaded successfully (${it.width}x${it.height})")
                } ?: Log.w(TAG, "Failed to load thumbnail for URL: $pdfUrl")
                
                withContext(Dispatchers.Main) {
                    bitmapState.value = bitmap
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in LaunchedEffect: ${e.message}", e)
            }
        }
        
        onDispose {
            job.cancel()
        }
    }
    
    return bitmapState
}
