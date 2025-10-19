package com.farouktouil.farouktouil.product_feature.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.farouktouil.farouktouil.R
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.deliverer_feature.presentation.DelivererViewModel

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.ui.theme.errorLight
import com.farouktouil.farouktouil.ui.theme.primaryContainerLight
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    delivererId: Int?, // Add delivererId as a parameter
    productViewModel: ProductViewModel = hiltViewModel()
) {
    // Set the selected deliverer in the ViewModel
    LaunchedEffect(delivererId) {
        productViewModel.selectDeliverer(delivererId)
    }

    val uiState by productViewModel.uiState.collectAsStateWithLifecycle()
    val isAddingProduct = remember { mutableStateOf(false) }
    val editingProduct = remember { mutableStateOf<Product?>(null) }
    val name = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val label = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("") }
    val minQuantity = remember { mutableStateOf("") }
    val maxQuantity = remember { mutableStateOf("") }
    val selectedDeliverer = remember { mutableStateOf(0) }
    val deliverers: List<DelivererEntity> by productViewModel.deliverers.collectAsStateWithLifecycle()
    val selectedDelivererId by productViewModel.selectedDelivererId.collectAsStateWithLifecycle()
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(id = R.string.products))
                        selectedDelivererId?.let { id ->
                            val delivererName = deliverers.find { it.delivererId == id }?.name ?: ""
                            Text(stringResource(id = R.string.filtered_by, delivererName), style = MaterialTheme.typography.bodySmall)
                        }
                        // Inventory Statistics
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.total_items, uiState.totalInventoryQuantity),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                stringResource(id = R.string.inventory_value, String.format("%.2f", uiState.totalInventoryValue)),
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (uiState.lowStockCount > 0) {
                                Text(
                                    stringResource(id = R.string.low_stock, uiState.lowStockCount),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    // Scan product button
                    IconButton(onClick = {
                        navController.navigate(ScreenRoutes.ScanProductScreen.route)
                    }) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = stringResource(id = R.string.scan_barcode)
                            )
                    }

                    // Filter button
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter by deliverer"
                            )
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            // Option to show all products
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.all_deliverers)) },
                                onClick = {
                                    productViewModel.selectDeliverer(null)
                                    showFilterMenu = false
                                },
                                enabled = selectedDelivererId != null
                            )
                            // List of deliverers to filter by
                            deliverers.forEach { deliverer ->
                                DropdownMenuItem(
                                    text = { Text(deliverer.name) },
                                    onClick = {
                                        productViewModel.selectDeliverer(deliverer.delivererId)
                                        showFilterMenu = false
                                    },
                                    enabled = selectedDelivererId != deliverer.delivererId
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isAddingProduct.value = true
                editingProduct.value = null
                name.value = ""
                price.value = ""
                label.value = ""
                quantity.value = ""
                minQuantity.value = ""
                maxQuantity.value = ""
                // Don't auto-select deliverer immediately, let user choose
                selectedDeliverer.value = 0
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isAddingProduct.value) {
                ProductForm(
                    name = name,
                    price = price,
                    label = label,
                    quantity = quantity,
                    minQuantity = minQuantity,
                    maxQuantity = maxQuantity,
                    selectedDeliverer = selectedDeliverer,
                    deliverers = deliverers,
                    onSave = {
                        val priceValue = price.value.toDoubleOrNull() ?: 0.0
                        val quantityValue = quantity.value.toIntOrNull() ?: 0
                        val minQuantityValue = minQuantity.value.toIntOrNull() ?: 0
                        val maxQuantityValue = maxQuantity.value.toIntOrNull() ?: 100

                        if (editingProduct.value != null) {
                            val updatedProduct = editingProduct.value!!.copy(
                                name = name.value,
                                label = label.value,
                                pricePerAmount = priceValue.toFloat(),
                                quantity = quantityValue,
                                minQuantity = minQuantityValue,
                                maxQuantity = maxQuantityValue,
                                belongsToDeliverer = selectedDeliverer.value
                            )
                            productViewModel.update(updatedProduct)
                        } else {
                            productViewModel.insert(
                                name = name.value,
                                label = label.value,
                                pricePerAmount = priceValue,
                                quantity = quantityValue,
                                minQuantity = minQuantityValue,
                                maxQuantity = maxQuantityValue,
                                belongsToDeliverer = selectedDeliverer.value
                            )
                        }
                        isAddingProduct.value = false
                    }
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.data) { product ->
                        val delivererName = deliverers.find { it.delivererId == product.belongsToDeliverer.toInt() }?.name ?: "Unknown"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    editingProduct.value = product
                                    name.value = product.name
                                    price.value = product.pricePerAmount.toString()
                                    label.value = product.label
                                    quantity.value = product.quantity.toString()
                                    minQuantity.value = product.minQuantity.toString()
                                    maxQuantity.value = product.maxQuantity.toString()
                                    selectedDeliverer.value = product.belongsToDeliverer
                                    isAddingProduct.value = true
                                },
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = product.name)
                                    if (product.label.isNotEmpty()) {
                                        Text(text = product.label, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(text = "Price: $${product.pricePerAmount}")
                                    Text(text = "Quantity: ${product.quantity}")
                                    Text(text = "Min: ${product.minQuantity}, Max: ${product.maxQuantity}")
                                    Text(text = "Deliverer: $delivererName")

                                    // Stock status indicator
                                    val stockStatus = when {
                                        product.quantity <= product.minQuantity -> "Low Stock"
                                        product.quantity >= product.maxQuantity -> "Over Stock"
                                        else -> "Normal Stock"
                                    }
                                    val stockColor = when {
                                        product.quantity <= product.minQuantity -> Color.Red
                                        product.quantity >= product.maxQuantity -> Color.Yellow
                                        else -> Color.Green
                                    }
                                    Text(text = stockStatus, color = stockColor, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = {
                                    editingProduct.value = product
                                    name.value = product.name
                                    price.value = product.pricePerAmount.toString()
                                    label.value = product.label
                                    quantity.value = product.quantity.toString()
                                    minQuantity.value = product.minQuantity.toString()
                                    maxQuantity.value = product.maxQuantity.toString()
                                    selectedDeliverer.value = product.belongsToDeliverer
                                    isAddingProduct.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = primaryContainerLight
                                    )
                                }
                                IconButton(onClick = { productViewModel.delete(product) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = errorLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductForm(
    name: MutableState<String>,
    price: MutableState<String>,
    label: MutableState<String>,
    quantity: MutableState<String>,
    minQuantity: MutableState<String>,
    maxQuantity: MutableState<String>,
    selectedDeliverer: MutableState<Int>,
    deliverers: List<DelivererEntity>,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    // Barcode scanning launcher setup
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // Set the scanned result as the product name
            name.value = result.contents
            Toast.makeText(context, "Scanned: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Scan button above product name input
        Button(
            onClick = {
                val options = ScanOptions().apply {
                    setPrompt("Scan a product barcode")
                    setBeepEnabled(true)
                    setOrientationLocked(true)
                    setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                    setCaptureActivity(PortraitCaptureActivity::class.java)
                }
                scanLauncher.launch(options)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan Barcode"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Barcode")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = label.value,
            onValueChange = { label.value = it },
            label = { Text("Product Label") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price.value,
            onValueChange = { price.value = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Quantity fields
        OutlinedTextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = minQuantity.value,
            onValueChange = { minQuantity.value = it },
            label = { Text("Minimum Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = maxQuantity.value,
            onValueChange = { maxQuantity.value = it },
            label = { Text("Maximum Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        var expanded by remember { mutableStateOf(false) }
        val selectedDelivererName = if (selectedDeliverer.value > 0) {
            deliverers.find { it.delivererId == selectedDeliverer.value }?.name
                ?: "Select Deliverer"
        } else {
            "Select Deliverer"
        }

        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDeliverer.value > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            ) {
                Text("Deliverer: $selectedDelivererName")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                deliverers.forEach { deliverer ->
                    DropdownMenuItem(
                        text = { Text("${deliverer.name} (ID: ${deliverer.delivererId})") },
                        onClick = {
                            selectedDeliverer.value = deliverer.delivererId
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSave
        ) {
            Text(text = "Save")
        }

        // Show message if deliverer not selected
        if (selectedDeliverer.value == 0 && deliverers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Please select a deliverer",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
