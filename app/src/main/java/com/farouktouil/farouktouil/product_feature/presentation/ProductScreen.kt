package com.farouktouil.farouktouil.product_feature.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.ui.theme.errorLight
import com.farouktouil.farouktouil.ui.theme.primaryContainerLight
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.farouktouil.farouktouil.product_feature.presentation.state.PersonnelListItem
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    structureNameArg: String?,
    productViewModel: ProductViewModel = hiltViewModel()
) {
    LaunchedEffect(structureNameArg) {
        productViewModel.selectStructure(structureNameArg)
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
    val selectedStructure = remember { mutableStateOf<String?>(null) }
    val selectedAccessories = remember { mutableStateOf(setOf<AccessoryType>()) }
    val structures: List<String> by productViewModel.structures.collectAsStateWithLifecycle()
    val selectedStructureName by productViewModel.selectedStructure.collectAsStateWithLifecycle()
    val personnel: List<PersonnelListItem> by productViewModel.personnel.collectAsStateWithLifecycle()
    val isPersonnelLoading by productViewModel.isPersonnelLoading.collectAsStateWithLifecycle()
    val personnelError by productViewModel.personnelError.collectAsStateWithLifecycle()
    val selectedPersonnel = remember { mutableStateOf<PersonnelListItem?>(null) }
    val productSearchQuery = remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val resolvePersonnel: (Product) -> PersonnelListItem? = { product ->
        personnel.firstOrNull { item ->
            product.assignedPersonnelId != null && item.id == product.assignedPersonnelId
        } ?: product.assignedPersonnelName?.takeIf { it.isNotBlank() }?.let { name ->
            PersonnelListItem(
                id = product.assignedPersonnelId,
                fullName = name,
                structureName = product.structureName
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                  modifier = Modifier.height(196.dp), // ✅ increase height (default is ~64.dp)
                title = {
                      Column {
                        Text(stringResource(id = R.string.products))
                        selectedStructureName?.takeIf { it.isNotBlank() }?.let { name ->
                            Text(
                                stringResource(id = R.string.filtered_by_structure, name),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // Inventory Statistics
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
//                            Text(
//                                stringResource(id = R.string.total_items, uiState.totalInventoryQuantity),
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                            Text(
//                                stringResource(id = R.string.inventory_value, String.format("%.2f", uiState.totalInventoryValue)),
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                            if (uiState.lowStockCount > 0) {
//                                Text(
//                                    stringResource(id = R.string.low_stock, uiState.lowStockCount),
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = Color.Red
//                                )
//                            }
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
                                contentDescription = "Filter structures"
                            )
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            // Option to show all products
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.all_structures)) },
                                onClick = {
                                    productViewModel.selectStructure(null)
                                    showFilterMenu = false
                                },
                                enabled = !selectedStructureName.isNullOrBlank()
                            )
                            structures.forEach { structure ->
                                DropdownMenuItem(
                                    text = { Text(structure) },
                                    onClick = {
                                        productViewModel.selectStructure(structure)
                                        showFilterMenu = false
                                    },
                                    enabled = selectedStructureName != structure
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
                selectedStructure.value = null
                selectedAccessories.value = emptySet()
                selectedPersonnel.value = null
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter Matériels et équipements")
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
                    selectedStructure = selectedStructure,
                    selectedPersonnel = selectedPersonnel,
                    selectedAccessories = selectedAccessories,
                    availableStructures = structures,
                    availablePersonnel = personnel,
                    isPersonnelLoading = isPersonnelLoading,
                    personnelError = personnelError,
                    onRefreshPersonnel = { productViewModel.refreshPersonnelDirectory() },
                    onSave = {
                        val priceValue = price.value.toDoubleOrNull() ?: 0.0
                        val quantityValue = quantity.value.toIntOrNull() ?: 0
                        val minQuantityValue = minQuantity.value.toIntOrNull() ?: 0
                        val maxQuantityValue = maxQuantity.value.toIntOrNull() ?: 100

                        val selectedPersonnelItem = selectedPersonnel.value
                        val accessoriesSelection = selectedAccessories.value

                        if (editingProduct.value != null) {
                            val updatedProduct = editingProduct.value!!.copy(
                                name = name.value,
                                label = label.value,
                                pricePerAmount = priceValue.toFloat(),
                                quantity = quantityValue,
                                minQuantity = minQuantityValue,
                                maxQuantity = maxQuantityValue,
                                structureName = selectedStructure.value,
                                assignedPersonnelId = selectedPersonnelItem?.id,
                                assignedPersonnelName = selectedPersonnelItem?.fullName
                            )
                            productViewModel.update(updatedProduct, accessoriesSelection)
                        } else {
                            productViewModel.insert(
                                name = name.value,
                                label = label.value,
                                pricePerAmount = priceValue,
                                quantity = quantityValue,
                                minQuantity = minQuantityValue,
                                maxQuantity = maxQuantityValue,
                                structureName = selectedStructure.value,
                                assignedPersonnelId = selectedPersonnelItem?.id,
                                assignedPersonnelName = selectedPersonnelItem?.fullName,
                                accessories = accessoriesSelection
                            )
                        }

                        selectedAccessories.value = emptySet()
                        isAddingProduct.value = false
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = productSearchQuery.value,
                        onValueChange = { productSearchQuery.value = it },
                        label = { Text("Rechercher un matériel") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    val filteredProducts = remember(productSearchQuery.value, uiState.data) {
                        val query = productSearchQuery.value.trim().lowercase()
                        if (query.isEmpty()) {
                            uiState.data
                        } else {
                            uiState.data.filter { product ->
                                val nameMatch = product.name.contains(query, ignoreCase = true)
                                val labelMatch = product.label.contains(query, ignoreCase = true)
                                val structureMatch = product.structureName?.contains(query, ignoreCase = true) ?: false
                                val personnelMatch = product.assignedPersonnelName?.contains(query, ignoreCase = true) ?: false
                                nameMatch || labelMatch || structureMatch || personnelMatch
                            }
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredProducts, key = { it.productId }) { product ->
                            var isMenuExpanded by remember { mutableStateOf(false) }

                            val openEditor: () -> Unit = {
                                editingProduct.value = product
                                name.value = product.name
                                label.value = product.label
                                selectedStructure.value = product.structureName
                                selectedAccessories.value = emptySet()
                                selectedPersonnel.value = resolvePersonnel(product)
                                coroutineScope.launch {
                                    selectedAccessories.value = productViewModel.getAccessoriesForProduct(product.productId)
                                }
                                isAddingProduct.value = true
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { openEditor() }
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
                                        product.structureName?.let { structure ->
                                            Text(text = stringResource(id = R.string.filtered_by_structure, structure))
                                        }
                                        product.assignedPersonnelName?.takeIf { it.isNotBlank() }?.let { assignee ->
                                            Text(
                                                text = "Affecté à : $assignee",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    Box {
                                        IconButton(onClick = { isMenuExpanded = true }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "More Options"
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = isMenuExpanded,
                                            onDismissRequest = { isMenuExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Modifier") },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = null,
                                                        tint = primaryContainerLight
                                                    )
                                                },
                                                onClick = {
                                                    isMenuExpanded = false
                                                    openEditor()
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Supprimer", color = errorLight) },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = errorLight
                                                    )
                                                },
                                                onClick = {
                                                    isMenuExpanded = false
                                                    productViewModel.delete(product)
                                                }
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductForm(
    name: MutableState<String>,
    price: MutableState<String>,
    label: MutableState<String>,
    quantity: MutableState<String>,
    minQuantity: MutableState<String>,
    maxQuantity: MutableState<String>,
    selectedStructure: MutableState<String?>,
    selectedPersonnel: MutableState<PersonnelListItem?>,
    selectedAccessories: MutableState<Set<AccessoryType>>,
    availableStructures: List<String>,
    availablePersonnel: List<PersonnelListItem>,
    isPersonnelLoading: Boolean,
    personnelError: String?,
    onRefreshPersonnel: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val accessoryGroups = remember { AccessoryType.values().toList().chunked(3) }

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
                    setPrompt("Scan un Matériel ou équipement barcode")
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
            label = { Text("Nom Matériels ou équipements") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = label.value,
            onValueChange = { label.value = it },
            label = { Text("Labelle du Matériels ou équipements ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Accessoires",
                style = MaterialTheme.typography.bodyMedium
            )
            accessoryGroups.forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTypes.forEach { accessoryType ->
                        val isSelected = accessoryType in selectedAccessories.value
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedAccessories.value = if (checked) {
                                        selectedAccessories.value + accessoryType
                                    } else {
                                        selectedAccessories.value - accessoryType
                                    }
                                }
                            )
                            Text(text = stringResource(id = accessoryType.labelRes))
                        }
                    }
                    repeat(3 - rowTypes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // OutlinedTextField(
        //     value = price.value,
        //     onValueChange = { price.value = it },
        //     label = { Text("Price") },
        //     modifier = Modifier.fillMaxWidth(),
        //     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        // )
        // Spacer(modifier = Modifier.height(8.dp))

        // // Quantity fields
        // OutlinedTextField(
        //     value = quantity.value,
        //     onValueChange = { quantity.value = it },
        //     label = { Text("Quantity") },
        //     modifier = Modifier.fillMaxWidth(),
        //     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        // )
        // Spacer(modifier = Modifier.height(8.dp))

        // OutlinedTextField(
        //     value = minQuantity.value,
        //     onValueChange = { minQuantity.value = it },
        //     label = { Text("Minimum Quantity") },
        //     modifier = Modifier.fillMaxWidth(),
        //     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        // )
        // Spacer(modifier = Modifier.height(8.dp))

        // OutlinedTextField(
        //     value = maxQuantity.value,
        //     onValueChange = { maxQuantity.value = it },
        //     label = { Text("Maximum Quantity") },
        //     modifier = Modifier.fillMaxWidth(),
        //     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        // )
        Spacer(modifier = Modifier.height(8.dp))

        val structures = remember(availableStructures) { availableStructures }
        var expanded by remember { mutableStateOf(false) }
        val selectedStructureName = selectedStructure.value ?: "Choisir une structure"
        val selectedPersonnelValue = selectedPersonnel.value
        val filteredPersonnel by remember(selectedStructure.value, availablePersonnel, selectedPersonnelValue) {
            derivedStateOf {
                val baseList = if (selectedStructure.value.isNullOrBlank()) {
                    availablePersonnel
                } else {
                    availablePersonnel.filter { it.structureName == selectedStructure.value }
                }

                if (selectedPersonnelValue != null && baseList.none {
                        it.id == selectedPersonnelValue.id && it.fullName == selectedPersonnelValue.fullName
                    }
                ) {
                    baseList + selectedPersonnelValue
                } else {
                    baseList
                }
            }
        }
        var personnelExpanded by remember { mutableStateOf(false) }
        val selectedPersonnelName = selectedPersonnelValue?.fullName ?: "Sélectionner un personnel"

        LaunchedEffect(selectedStructure.value) {
            val structure = selectedStructure.value
            val current = selectedPersonnel.value
            if (structure != null && current != null && current.structureName != null && current.structureName != structure) {
                selectedPersonnel.value = null
            }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { shouldExpand ->
                expanded = shouldExpand && structures.isNotEmpty()
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedStructureName,
                onValueChange = {},
                label = { Text("Structure") },
                readOnly = true,
                enabled = structures.isNotEmpty(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                structures.forEach { structureName ->
                    DropdownMenuItem(
                        text = { Text(structureName) },
                        onClick = {
                            selectedStructure.value = structureName
                            selectedPersonnel.value = null
                            expanded = false
                        }
                    )
                }
            }
        }

        if (structures.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Aucune structure disponible. Ajoutez-en depuis l'écran Structures.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = personnelExpanded,
            onExpandedChange = { shouldExpand ->
                personnelExpanded = shouldExpand && filteredPersonnel.isNotEmpty()
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedPersonnelName,
                onValueChange = {},
                label = { Text("Personnel") },
                readOnly = true,
                enabled = filteredPersonnel.isNotEmpty(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = personnelExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = personnelExpanded,
                onDismissRequest = { personnelExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Aucun personnel") },
                    onClick = {
                        selectedPersonnel.value = null
                        personnelExpanded = false
                    }
                )
                filteredPersonnel.forEach { personnelItem ->
                    DropdownMenuItem(
                        text = {
                            val structureSuffix = personnelItem.structureName?.let { " (${it})" } ?: ""
                            Text(personnelItem.fullName + structureSuffix)
                        },
                        onClick = {
                            selectedPersonnel.value = personnelItem
                            personnelExpanded = false
                        }
                    )
                }
            }
        }

        if (isPersonnelLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        personnelError?.let { errorMessage ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!isPersonnelLoading && filteredPersonnel.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (selectedStructure.value.isNullOrEmpty()) {
                    "Aucun personnel disponible pour l'instant."
                } else {
                    "Aucun personnel trouvé pour cette structure."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = onRefreshPersonnel) {
            Text("Actualiser le personnel")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSave
        ) {
            Text(text = "Enregistrer")
        }

        // Show message if deliverer not selected
        if (selectedStructure.value.isNullOrEmpty() && structures.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "prière de selectionner une structure",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
