package com.farouktouil.farouktouil.core.presentation

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farouktouil.farouktouil.feature.pdfviewer.PdfViewerViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

@Composable
fun PdfViewer(url: String, modifier: Modifier = Modifier,  viewModel: PdfViewerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pdfState by remember { mutableStateOf<PdfState?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Use a key to recompose and restart the download when the URL changes
    LaunchedEffect(key1 = url) {
        isLoading = true
        errorMessage = null
        pdfState?.close() // Close previous state if any
        pdfState = null

        val job = coroutineScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw IOException("Unexpected response code: ${response.code}")
                }

                val pdfBytes = response.body?.bytes() ?: throw IOException("Empty response body")
                
                if (!isActive) return@launch

                val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.pdf")
                file.writeBytes(pdfBytes)

                val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfState = PdfState(PdfRenderer(parcelFileDescriptor), coroutineScope)
            } catch (e: Exception) {
                if (isActive) {
                    errorMessage = e.message ?: "An error occurred while loading the PDF."
                    e.printStackTrace()
                }
            } finally {
                if (isActive) {
                    isLoading = false
                }
            }
        }

        job.invokeOnCompletion { cause ->
            if (cause != null && cause !is kotlinx.coroutines.CancellationException) {
                errorMessage = cause.message ?: "An unknown error occurred."
                isLoading = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            pdfState?.close()
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text(
                text = errorMessage!!, 
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            pdfState != null -> {
                PdfRendererComposable(pdfState = pdfState!!)
            }
            else -> {
                Text("Select a document to view.")
            }
        }
    }
}

class PdfState(
    private val renderer: PdfRenderer,
    private val coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val pageCount: Int = renderer.pageCount
    private val mutex = Mutex()
    private val bitmaps = mutableMapOf<Int, Bitmap>()

    fun loadPage(index: Int): State<Bitmap?> {
        val state = mutableStateOf<Bitmap?>(null)
        coroutineScope.launch(Dispatchers.IO) {
            mutex.withLock {
                try {
                    if (!bitmaps.containsKey(index)) {
                        renderer.openPage(index).use { page ->
                            val bitmap = Bitmap.createBitmap(
                                page.width, 
                                page.height, 
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmaps[index] = bitmap
                        }
                    }
                    state.value = bitmaps[index]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return state
    }

    fun close() {
        // Clear cache and close renderer
        bitmaps.values.forEach { it.recycle() }
        bitmaps.clear()
        renderer.close()
    }
}

@Composable
fun PdfRendererComposable(pdfState: PdfState) {
    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Controller
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page")
            }
            Text(text = "Page ${currentPage + 1} / ${pdfState.pageCount}")
            IconButton(
                onClick = { if (currentPage < pdfState.pageCount - 1) currentPage++ },
                enabled = currentPage < pdfState.pageCount - 1
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page")
            }
        }

        // PDF Page
        val pageBitmap by pdfState.loadPage(currentPage)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (pageBitmap != null) {
                Image(
                    bitmap = pageBitmap!!.asImageBitmap(),
                    contentDescription = "Page ${currentPage + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            } else {
                CircularProgressIndicator()
            }
        }
    }
}