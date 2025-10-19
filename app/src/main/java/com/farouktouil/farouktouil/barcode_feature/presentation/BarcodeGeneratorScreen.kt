package com.farouktouil.farouktouil.barcode_feature.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.farouktouil.farouktouil.R
import com.farouktouil.farouktouil.product_feature.presentation.ProductViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class BarcodeItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val productName: String,
    val quantity: Int,
    val bitmap: Bitmap
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeGeneratorScreen(
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = hiltViewModel()
    var barcodeText by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    val barcodeItems = remember { mutableStateListOf<BarcodeItem>() }
    var isGenerating by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scannedProduct by remember { mutableStateOf<String?>(null) }

    // Permission request for camera
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner = true
        } else {
            Toast.makeText(context, "Camera permission is required to scan barcodes", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.barcode_generator)) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                    }
                }
            )
        },
        floatingActionButton = {
            if (barcodeItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            exportToPdf(context, barcodeItems)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = stringResource(R.string.export_to_pdf))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Scan Product Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Scan Product for Details",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                showScanner = true
                            } else {
                                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Product Barcode")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (scannedProduct != null) {
                        Text(
                            text = "Scanned: $scannedProduct",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input section for generating barcodes
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.generate_barcode),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text(stringResource(R.string.product_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = barcodeText,
                        onValueChange = { barcodeText = it },
                        label = { Text(stringResource(R.string.barcode_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text(stringResource(R.string.quantity_to_print)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (barcodeText.isNotEmpty() && productName.isNotEmpty() && quantity.isNotEmpty()) {
                                val qty = quantity.toIntOrNull() ?: 1
                                if (qty > 0) {
                                    isGenerating = true
                                    scope.launch {
                                        val bitmap = generateBarcode(barcodeText)
                                        if (bitmap != null) {
                                            barcodeItems.add(
                                                BarcodeItem(
                                                    text = barcodeText,
                                                    productName = productName,
                                                    quantity = qty,
                                                    bitmap = bitmap
                                                )
                                            )
                                            barcodeText = ""
                                            productName = ""
                                            quantity = "1"
                                            Toast.makeText(context, context.getString(R.string.barcode_generated), Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.failed_to_generate), Toast.LENGTH_SHORT).show()
                                        }
                                        isGenerating = false
                                    }
                                } else {
                                    Toast.makeText(context, context.getString(R.string.quantity_must_be_greater_than_zero), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isGenerating) stringResource(R.string.generating) else stringResource(R.string.generate_barcode))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Generated barcodes list
            if (barcodeItems.isNotEmpty()) {
                val totalQuantity = barcodeItems.sumOf { it.quantity }
                Text(
                    text = stringResource(R.string.generated_barcodes, barcodeItems.size, totalQuantity),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(barcodeItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.quantity, item.quantity),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    bitmap = item.bitmap.asImageBitmap(),
                                    contentDescription = "Barcode",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.text,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = { barcodeItems.remove(item) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Camera Scanner
    if (showScanner) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    DecoratedBarcodeView(ctx).apply {
                        decoderFactory = DefaultDecoderFactory(
                            listOf(
                                com.google.zxing.BarcodeFormat.CODE_128,
                                com.google.zxing.BarcodeFormat.CODE_39,
                                com.google.zxing.BarcodeFormat.CODE_93,
                                com.google.zxing.BarcodeFormat.CODABAR,
                                com.google.zxing.BarcodeFormat.EAN_13,
                                com.google.zxing.BarcodeFormat.EAN_8,
                                com.google.zxing.BarcodeFormat.UPC_A,
                                com.google.zxing.BarcodeFormat.UPC_E,
                                com.google.zxing.BarcodeFormat.ITF,
                                com.google.zxing.BarcodeFormat.DATA_MATRIX,
                                com.google.zxing.BarcodeFormat.QR_CODE
                            )
                        )
                        initializeFromIntent(android.content.Intent())
                        decodeContinuous(object : BarcodeCallback {
                            override fun barcodeResult(result: BarcodeResult?) {
                                result?.let {
                                    scannedProduct = it.text
                                    showScanner = false
                                    scope.launch {
                                        val product = productViewModel.getProductByBarcode(it.text)
                                        if (product != null) {
                                            Toast.makeText(ctx, "Product: ${product.name}", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(ctx, "Product not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        })
                        resume()
                    }
                }
            )
            Button(onClick = { showScanner = false }) {
                Text("Close Scanner")
            }
        }
    }
}

private suspend fun generateBarcode(text: String): Bitmap? {
    return withContext(Dispatchers.Default) {
        try {
            val width = 600
            val height = 200
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                text,
                BarcodeFormat.CODE_128,
                width,
                height
            )
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private suspend fun exportToPdf(context: Context, barcodeItems: List<BarcodeItem>) {
    withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            
            // Layout configuration
            val margin = 40f
            val barcodeWidth = 250f
            val barcodeHeight = 100f
            val barcodesPerRow = 2
            val barcodesPerColumn = 5
            val barcodesPerPage = barcodesPerRow * barcodesPerColumn
            
            val horizontalSpacing = (pageWidth - 2 * margin - barcodesPerRow * barcodeWidth) / (barcodesPerRow - 1)
            val verticalSpacing = (pageHeight - 2 * margin - barcodesPerColumn * (barcodeHeight + 40)) / (barcodesPerColumn - 1)
            
            var currentPage = 1
            var barcodeCount = 0
            
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = page.canvas
            val paint = Paint()
            paint.isAntiAlias = true
            
            // Expand items based on quantity
            val expandedItems = mutableListOf<BarcodeItem>()
            barcodeItems.forEach { item ->
                repeat(item.quantity) {
                    expandedItems.add(item)
                }
            }
            
            expandedItems.forEachIndexed { index, item ->
                if (barcodeCount >= barcodesPerPage) {
                    pdfDocument.finishPage(page)
                    currentPage++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    barcodeCount = 0
                }
                
                val row = barcodeCount / barcodesPerRow
                val col = barcodeCount % barcodesPerRow
                
                val xPosition = margin + col * (barcodeWidth + horizontalSpacing)
                val yPosition = margin + row * (barcodeHeight + 40 + verticalSpacing)
                
                // Draw product name (small, above barcode)
                paint.textSize = 12f
                paint.color = Color.BLACK
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(
                    item.productName,
                    xPosition + barcodeWidth / 2,
                    yPosition,
                    paint
                )
                
                // Draw barcode image
                val scaledBitmap = Bitmap.createScaledBitmap(
                    item.bitmap,
                    barcodeWidth.toInt(),
                    barcodeHeight.toInt(),
                    false
                )
                canvas.drawBitmap(scaledBitmap, xPosition, yPosition + 15, paint)
                
                // Draw barcode number UNDER the barcode
                paint.textSize = 14f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(
                    item.text,
                    xPosition + barcodeWidth / 2,
                    yPosition + barcodeHeight + 30,
                    paint
                )
                
                barcodeCount++
            }
            
            pdfDocument.finishPage(page)
            
            // Save PDF
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Barcodes_$timestamp.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            
            pdfDocument.close()
            
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "PDF saved to Downloads/$fileName",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Failed to export PDF: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
