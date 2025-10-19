package com.farouktouil.farouktouil.product_feature.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.farouktouil.farouktouil.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanProductScreen(
    onOpenDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProductViewModel = hiltViewModel()
    var torchOn by remember { mutableStateOf(false) }
    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var scannedRaw by remember { mutableStateOf<String?>(null) }
    var scannedNormalized by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    // Live search matches
    val products = viewModel.uiState.collectAsState().value.data
    val matches by remember(searchQuery, products) {
        derivedStateOf {
            val q = searchQuery.trim()
            if (q.isEmpty()) emptyList() else products.filter { it.name.contains(q, ignoreCase = true) }
        }
    }

    // Permission state
    val cameraPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        cameraPermissionGranted.value = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission is required to scan barcodes", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.scan_barcode)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search bar placed above camera view
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(searchFocusRequester),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text(stringResource(id = R.string.search_hint)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    val raw = searchQuery.trim()
                    if (raw.isNotEmpty()) {
                        coroutineScope.launch {
                            var handled = false
                            try {
                                val byName = viewModel.getProductByName(raw)
                                if (byName != null) {
                                    handled = true
                                    onOpenDetails(byName.productId.toString())
                                }
                            } catch (e: Exception) {
                                // ignore
                            }

                            if (!handled) {
                                val normalized = raw.filter { ch -> ch.isDigit() }
                                try {
                                    val byBarcode = viewModel.getProductByBarcode(normalized)
                                    if (byBarcode != null) {
                                        handled = true
                                        onOpenDetails(byBarcode.productId.toString())
                                    }
                                } catch (e: Exception) {
                                    // ignore
                                }
                            }

                            if (!handled) {
                                val normalized = raw.filter { ch -> ch.isDigit() }
                                onOpenDetails(if (normalized.isNotEmpty()) normalized else raw)
                            }

                            // Clear search and dismiss keyboard
                            // keep query so user sees results; optionally clear focus
                            //searchQuery = ""
                            focusManager.clearFocus()
                        }
                    }
                })
            )
            // Live results list while typing
            if (matches.isNotEmpty()) {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    items(matches) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                // navigate to details for selected product
                                onOpenDetails(product.productId.toString())
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = product.label ?: "", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (cameraPermissionGranted.value) {
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

                                initializeFromIntent(Intent())

                                decodeContinuous(object : BarcodeCallback {
                                    override fun barcodeResult(result: BarcodeResult?) {
                                        result?.let {
                                            if (isScanning && it.text != lastScannedCode) {
                                                lastScannedCode = it.text
                                                isScanning = false

                                                // Normalize scanned barcode: trim and keep digits only
                                                val raw = it.text ?: ""
                                                val normalized = raw.trim().filter { ch -> ch.isDigit() }

                                                // Update states (actual DB lookup/navigation happens in composable scope)
                                                scannedRaw = raw
                                                scannedNormalized = normalized

                                                // Quick feedback
                                                Toast.makeText(context, "Scanned: '$raw' -> '$normalized'", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }

                                    override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
                                        // no-op
                                    }
                                })

                                cameraSettings.apply {
                                    isAutoFocusEnabled = true
                                    isContinuousFocusEnabled = true
                                    isBarcodeSceneModeEnabled = true
                                }

                                resume()
                            }
                        },
                        update = { view ->
                            if (torchOn) view.setTorchOn() else view.setTorchOff()
                        }
                    )

                    if (isScanning) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.QrCodeScanner,
                                contentDescription = "Scanning",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(id = R.string.scanning_for_barcode), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    // When a barcode is captured by the AndroidView, perform lookup/navigation here
                    scannedRaw?.let { raw ->
                        val normalized = scannedNormalized ?: raw.trim().filter { ch -> ch.isDigit() }

                        LaunchedEffect(raw, normalized) {
                            var handled = false
                            try {
                                val byName = viewModel.getProductByName(raw)
                                if (byName != null) {
                                    handled = true
                                    onOpenDetails(byName.productId.toString())
                                }
                            } catch (e: Exception) {
                                // ignore
                            }

                            if (!handled) {
                                try {
                                    val byBarcode = viewModel.getProductByBarcode(normalized)
                                    if (byBarcode != null) {
                                        handled = true
                                        onOpenDetails(byBarcode.productId.toString())
                                    }
                                } catch (e: Exception) {
                                    // ignore
                                }
                            }

                            if (!handled) {
                                // fallback to opening by barcode string
                                onOpenDetails(normalized)
                            }

                            // reset scanning state so user can rescan later if needed
                            isScanning = true
                            scannedRaw = null
                            scannedNormalized = null
                        }
                    }
                } else {
                    // Permission not granted - show explanation + request button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "Camera permission is required to scan products.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }) {
                                Text(stringResource(id = R.string.grant_permission))
                            }

                            OutlinedButton(onClick = {
                                // Open app settings so the user can enable permission
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }) {
                                Text(stringResource(id = R.string.open_settings))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    }
}
