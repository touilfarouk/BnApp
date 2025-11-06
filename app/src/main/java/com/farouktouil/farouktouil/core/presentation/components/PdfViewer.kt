package com.farouktouil.farouktouil.core.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.farouktouil.farouktouil.consultation_feature.domain.model.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var pageCount by remember { mutableIntStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val tempFile = remember { File(context.cacheDir, "temp_pdf.pdf") }

    // Download and display PDF
    LaunchedEffect(pdfUrl) {
        try {
            isLoading = true
            error = null

            // Download PDF to a temporary file
            withContext(Dispatchers.IO) {
                try {
                    val url = java.net.URL(pdfUrl)
                    val connection = url.openConnection()
                    connection.connect()

                    val inputStream = connection.getInputStream()
                    val outputStream = FileOutputStream(tempFile)

                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Open the PDF file
                    val parcelFileDescriptor = android.os.ParcelFileDescriptor.open(
                        tempFile,
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
                    
                    // Empty view for layout balance
                    Spacer(modifier = Modifier.width(64.dp))
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
                            Image(
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = "PDF Page ${currentPage + 1}",
                                modifier = Modifier.fillMaxWidth()
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
            }
        }
    }
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
