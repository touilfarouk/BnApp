package com.farouktouil.farouktouil.product_feature.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.farouktouil.farouktouil.ui.theme.FaroukTouilTheme
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class BarcodeScannerActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with scanning
        } else {
            Toast.makeText(this, "Camera permission is required to scan barcodes", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, show scanner UI
            setContent {
                FaroukTouilTheme {
                    BarcodeScannerScreen(
                        onBarcodeScanned = { barcode ->
                            // Fetch product details and return
                            val intent = Intent().apply {
                                putExtra("SCANNED_BARCODE", barcode)
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        },
                        onCancel = {
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                    )
                }
            }
        } else {
            // Request permission and setContent once granted in the launcher callback
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun BarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProductViewModel = hiltViewModel()
    var torchOn by remember { mutableStateOf(false) }
    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var scannedProduct by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with instructions and torch toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan Product Barcode",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Point camera at barcode to scan",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Torch toggle button
                OutlinedButton(
                    onClick = { torchOn = !torchOn }
                ) {
                    Text(if (torchOn) "Turn Off Flash" else "Turn On Flash")
                }
            }
        }

        // Barcode scanner view
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    DecoratedBarcodeView(context).apply {
                        // Configure decoder for product barcodes
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

                        initializeFromIntent(Intent())

                        // Set up continuous scanning
                        decodeContinuous(object : BarcodeCallback {
                            override fun barcodeResult(result: BarcodeResult?) {
                                result?.let {
                                    if (isScanning && it.text != lastScannedCode) {
                                        lastScannedCode = it.text
                                        isScanning = false

                                        // Provide haptic feedback
                                        try {
                                            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(100)
                                            }
                                        } catch (e: Exception) {
                                            // Handle vibration not available
                                        }

                                        // Fetch product details
                                        scannedProduct = it.text
                                        onBarcodeScanned(it.text)
                                    }
                                }
                            }

                            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
                                // Handle possible result points if needed
                            }
                        })

                        // Configure camera settings for better scanning
                        cameraSettings.apply {
                            isAutoFocusEnabled = true
                            isContinuousFocusEnabled = true
                            isBarcodeSceneModeEnabled = true
                        }

                        // Start camera
                        resume()
                    }
                },
                update = { view ->
                    // Update torch state
                    if (torchOn) {
                        view.setTorchOn()
                    } else {
                        view.setTorchOff()
                    }
                }
            )

            // Real-time scanning indicator
            if (isScanning) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.QrCodeScanner,
                            contentDescription = "Scanning Barcode",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scanning for barcode...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Align barcode with camera",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Bottom action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}
