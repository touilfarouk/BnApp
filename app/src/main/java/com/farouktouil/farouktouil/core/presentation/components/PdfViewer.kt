package com.farouktouil.farouktouil.core.presentation.components

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.farouktouil.farouktouil.consultation_feature.domain.model.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

@Composable
fun DocumentList(
    documents: List<Document>,
    modifier: Modifier = Modifier,
    onDocumentClick: (Document) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(documents) { document ->
            DocumentItem(
                document = document,
                onClick = { onDocumentClick(document) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentItem(
    document: Document,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = document.fileName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = document.fileUrl,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerDialog(
    pdfUrl: String,
    onDismiss: () -> Unit,
    localFilePath: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var pageCount by remember { mutableIntStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }

    val tempFile = remember { File(context.cacheDir, "temp_pdf.pdf") }
    val documentName = remember(pdfUrl) {
        Uri.parse(pdfUrl).lastPathSegment ?: "document.pdf"
    }

    // Download and display PDF
    LaunchedEffect(pdfUrl) {
        try {
            isLoading = true
            error = null

            withContext(Dispatchers.IO) {
                try {
                    val sourceFile = resolveSourceFile(localFilePath, tempFile, pdfUrl)

                    val parcelFileDescriptor = android.os.ParcelFileDescriptor.open(
                        sourceFile,
                        android.os.ParcelFileDescriptor.MODE_READ_ONLY
                    )

                    pdfRenderer = PdfRenderer(parcelFileDescriptor).also { renderer ->
                        pageCount = renderer.pageCount
                        if (pageCount > 0) {
                            renderer.openPage(currentPage).use { page ->
                                val newBitmap = Bitmap.createBitmap(
                                    page.width,
                                    page.height,
                                    Bitmap.Config.ARGB_8888
                                )
                                page.render(newBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                bitmap = newBitmap
                                scale = 1f
                            }
                        }
                    }
                } catch (e: Exception) {
                    error = "Failed to load PDF: ${e.message}"
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
            isLoading = false
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
            bitmap?.recycle()
            tempFile.delete()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    
                    Text(
                        text = "${currentPage + 1} / $pageCount",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = {
                            enqueuePdfDownload(context, pdfUrl, documentName)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Télécharger le document"
                        )
                    }
                }

                // PDF Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator()
                        }
                        error != null -> {
                            Text(
                                text = error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        bitmap != null -> {
                            val scaledModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .pointerInput(currentPage, bitmap) {
                                    detectTransformGestures { _, _, zoom, _ ->
                                        val newScale = (scale * zoom).coerceIn(1f, 4f)
                                        scale = newScale
                                    }
                                }
                                .pointerInput(currentPage) {
                                    detectTapGestures(onDoubleTap = {
                                        scale = if (scale > 1f) 1f else 2f
                                    })
                                }
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale
                                )

                            Image(
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = "PDF Page ${currentPage + 1}",
                                modifier = scaledModifier
                            )
                        }
                    }
                }

                // Navigation Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentPage > 0) {
                                currentPage--
                                updatePdfPage(currentPage, pdfRenderer) { newBitmap ->
                                    bitmap?.recycle()
                                    bitmap = newBitmap
                                }
                            }
                        },
                        enabled = currentPage > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Page"
                        )
                    }

                    Text("Page ${currentPage + 1} of $pageCount")

                    IconButton(
                        onClick = {
                            if (currentPage < pageCount - 1) {
                                currentPage++
                                updatePdfPage(currentPage, pdfRenderer) { newBitmap ->
                                    bitmap?.recycle()
                                    bitmap = newBitmap
                                }
                            }
                        },
                        enabled = currentPage < (pageCount - 1)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Page"
                        )
                    }
                }

                if (bitmap != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Slider(
                            value = scale,
                            onValueChange = { scale = it.coerceIn(1f, 4f) },
                            valueRange = 1f..4f,
                            steps = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Zoom: ${(scale * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

private fun enqueuePdfDownload(context: Context, pdfUrl: String, fileName: String) {
    try {
        val sanitizedName = if (fileName.lowercase().endsWith(".pdf")) fileName else "$fileName.pdf"
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
            .setTitle(sanitizedName)
            .setDescription("Téléchargement du document")
            .setMimeType("application/pdf")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, sanitizedName)
            .setAllowedOverMetered(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        if (downloadManager != null) {
            downloadManager.enqueue(request)
            Toast.makeText(context, "Téléchargement démarré", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Service de téléchargement indisponible", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Échec du téléchargement", Toast.LENGTH_LONG).show()
        Log.e("PdfViewer", "Error enqueueing download", e)
    }
}

private fun resolveSourceFile(
    localFilePath: String?,
    tempFile: File,
    pdfUrl: String
): File {
    localFilePath?.let { path ->
        val cachedFile = File(path)
        if (cachedFile.exists()) {
            return cachedFile
        }
    }

    val connection = URL(pdfUrl).openConnection()
    connection.connect()

    connection.getInputStream().use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}

private fun updatePdfPage(
    pageIndex: Int,
    pdfRenderer: PdfRenderer?,
    onBitmapReady: (Bitmap) -> Unit
) {
    pdfRenderer?.let { renderer ->
        try {
            renderer.openPage(pageIndex).use { page ->
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                onBitmapReady(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
