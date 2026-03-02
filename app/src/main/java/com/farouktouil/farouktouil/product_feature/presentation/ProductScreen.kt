package com.farouktouil.farouktouil.product_feature.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.R
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.product_feature.presentation.state.PersonnelListItem
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    var structureFilterSelection by remember { mutableStateOf<String?>(null) }
    var personnelFilterSelection by remember { mutableStateOf<PersonnelListItem?>(null) }
    var inlineFiltersExpanded by remember { mutableStateOf(true) }
    var structureFilterMenuExpanded by remember { mutableStateOf(false) }
    var personnelFilterMenuExpanded by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val resolvePersonnel: (Product) -> PersonnelListItem? = { product ->
        personnel.firstOrNull { item ->
            product.assignedPersonnelId != null && item.id == product.assignedPersonnelId
        } ?: product.assignedPersonnelName?.takeIf { it.isNotBlank() }?.let { personnelName ->
            PersonnelListItem(
                id = product.assignedPersonnelId,
                fullName = personnelName,
                structureName = product.structureName
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(64.dp), 
                title = {
                    Column {
                        Text(
                            stringResource(id = R.string.products),
                            color = Color.White
                        )
                        selectedStructureName?.takeIf { it.isNotBlank() }?.let { name ->
                            Text(
                                stringResource(id = R.string.filtered_by_structure, name),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF2E7D32), 
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20), 
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(ScreenRoutes.ScanProductScreen.route)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF1B5E20), 
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(id = R.string.scan_barcode)
                        )
                    }

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
            FloatingActionButton(
                onClick = {
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
                },
                containerColor = Color(0xFF1B5E20), 
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter Matériels et équipements"
                )
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
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF212121) 
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recherche & filtres",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(onClick = { inlineFiltersExpanded = !inlineFiltersExpanded }) {
                                    Text(if (inlineFiltersExpanded) "Masquer" else "Afficher")
                                }
                            }
                            if (inlineFiltersExpanded) {
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
                                    modifier = Modifier.fillMaxWidth()
                                )
                                ExposedDropdownMenuBox(
                                    expanded = structureFilterMenuExpanded,
                                    onExpandedChange = { structureFilterMenuExpanded = !structureFilterMenuExpanded }
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        value = structureFilterSelection ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Structure") },
                                        placeholder = { Text("Toutes les structures") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = structureFilterMenuExpanded)
                                        }
                                    )
                                    ExposedDropdownMenu(
                                        expanded = structureFilterMenuExpanded,
                                        onDismissRequest = { structureFilterMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Toutes les structures") },
                                            onClick = {
                                                structureFilterSelection = null
                                                personnelFilterSelection = null
                                                structureFilterMenuExpanded = false
                                            }
                                        )
                                        structures.forEach { structure ->
                                            DropdownMenuItem(
                                                text = { Text(structure) },
                                                onClick = {
                                                    structureFilterSelection = structure
                                                    personnelFilterSelection = null
                                                    structureFilterMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                val personnelFilterOptions = remember(structureFilterSelection, personnel) {
                                    val base = if (structureFilterSelection.isNullOrBlank()) {
                                        personnel
                                    } else {
                                        personnel.filter {
                                            it.structureName?.equals(structureFilterSelection, ignoreCase = true) == true
                                        }
                                    }
                                    base.distinctBy { it.id ?: it.fullName }
                                }
                                ExposedDropdownMenuBox(
                                    expanded = personnelFilterMenuExpanded,
                                    onExpandedChange = {
                                        if (personnelFilterOptions.isNotEmpty()) {
                                            personnelFilterMenuExpanded = !personnelFilterMenuExpanded
                                        }
                                    }
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        value = personnelFilterSelection?.fullName ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = personnelFilterOptions.isNotEmpty(),
                                        label = { Text("Personnel") },
                                        placeholder = { Text("Tous les personnels") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = personnelFilterMenuExpanded)
                                        }
                                    )
                                    ExposedDropdownMenu(
                                        expanded = personnelFilterMenuExpanded,
                                        onDismissRequest = { personnelFilterMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Tous les personnels") },
                                            onClick = {
                                                personnelFilterSelection = null
                                                personnelFilterMenuExpanded = false
                                            }
                                        )
                                        personnelFilterOptions.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(item.fullName) },
                                                onClick = {
                                                    personnelFilterSelection = item
                                                    personnelFilterMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val filteredProducts = remember(
                        productSearchQuery.value,
                        structureFilterSelection,
                        personnelFilterSelection,
                        uiState.data
                    ) {
                        val query = productSearchQuery.value.trim().lowercase()
                        val structureFilter = structureFilterSelection
                        val personnelFilter = personnelFilterSelection

                        uiState.data.filter { product ->
                            val matchesQuery = if (query.isEmpty()) {
                                true
                            } else {
                                val nameMatch = product.name.contains(query, ignoreCase = true)
                                val labelMatch = product.label.contains(query, ignoreCase = true)
                                val structureMatch = product.structureName?.contains(query, ignoreCase = true) ?: false
                                val personnelMatch = product.assignedPersonnelName?.contains(query, ignoreCase = true) ?: false
                                nameMatch || labelMatch || structureMatch || personnelMatch
                            }

                            val matchesStructure = structureFilter?.let { filter ->
                                product.structureName?.equals(filter, ignoreCase = true) ?: false
                            } ?: true

                            val matchesPersonnel = personnelFilter?.let { filter ->
                                val matchesId = filter.id != null && product.assignedPersonnelId == filter.id
                                val matchesName = product.assignedPersonnelName?.equals(filter.fullName, ignoreCase = true) == true
                                matchesId || matchesName
                            } ?: true

                            matchesQuery && matchesStructure && matchesPersonnel
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

                            val cardBackground = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clickable { openEditor() }
                                    .shadow(
                                        elevation = 1.dp,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        if (product.label.isNotEmpty()) {
                                            Text(
                                                text = product.label,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        
                                        product.structureName?.let { structure ->
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Business,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = structure,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        
                                        product.assignedPersonnelName?.takeIf { it.isNotBlank() }?.let { assignee ->
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Affecté à : $assignee",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    
                                    Box {
                                        IconButton(
                                            onClick = { isMenuExpanded = true },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "Options",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = isMenuExpanded,
                                            onDismissRequest = { isMenuExpanded = false },
                                            modifier = Modifier
                                                .width(200.dp)
                                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                        ) {
                                            DropdownMenuItem(
                                                text = { 
                                                    Text(
                                                        "Modifier",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    ) 
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                },
                                                colors = MenuDefaults.itemColors(
                                                    textColor = MaterialTheme.colorScheme.onSurface,
                                                    leadingIconColor = MaterialTheme.colorScheme.primary,
                                                    trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                                ),
                                                onClick = {
                                                    isMenuExpanded = false
                                                    openEditor()
                                                }
                                            )
                                            
                                            DropdownMenuItem(
                                                text = { 
                                                    Text(
                                                        "Supprimer",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.error
                                                    ) 
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                },
                                                colors = MenuDefaults.itemColors(
                                                    textColor = MaterialTheme.colorScheme.error,
                                                    leadingIconColor = MaterialTheme.colorScheme.error,
                                                    disabledTextColor = MaterialTheme.colorScheme.error.copy(alpha = 0.38f),
                                                    disabledLeadingIconColor = MaterialTheme.colorScheme.error.copy(alpha = 0.38f)
                                                ),
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
    val accessoryGroups = remember { AccessoryType.entries.chunked(3) }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            name.value = result.contents
            Toast.makeText(context, "Scanned: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan Barcode",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scanner le code-barres")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { 
                Text(
                    "Nom Matériels ou équipements",
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = label.value,
            onValueChange = { label.value = it },
            label = { 
                Text(
                    "Labelle du Matériels ou équipements",
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Accessoires",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
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
                            Text(
                                text = stringResource(id = accessoryType.labelRes),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    repeat(3 - rowTypes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val structures = remember(availableStructures) { availableStructures }
        var expanded by remember { mutableStateOf(false) }
        val selectedStructureNameValue = selectedStructure.value ?: "Choisir une structure"
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                value = selectedStructureNameValue,
                onValueChange = {},
                label = { 
                    Text(
                        "Structure",
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                readOnly = true,
                enabled = structures.isNotEmpty(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorTrailingIconColor = MaterialTheme.colorScheme.error,
                    errorLeadingIconColor = MaterialTheme.colorScheme.error
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(12.dp)
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                value = selectedPersonnelName,
                onValueChange = {},
                label = { 
                    Text(
                        "Personnel",
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                readOnly = true,
                enabled = filteredPersonnel.isNotEmpty(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = personnelExpanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorTrailingIconColor = MaterialTheme.colorScheme.error,
                    errorLeadingIconColor = MaterialTheme.colorScheme.error
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(12.dp)
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
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }

        personnelError?.let { errorMessage ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
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

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRefreshPersonnel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualiser")
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enregistrer")
            }
        }

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

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ProductFormPreview() {
    MaterialTheme {
        ProductForm(
            name = remember { mutableStateOf("Computer") },
            price = remember { mutableStateOf("1000") },
            label = remember { mutableStateOf("BNEDER-2024-001") },
            quantity = remember { mutableStateOf("10") },
            minQuantity = remember { mutableStateOf("5") },
            maxQuantity = remember { mutableStateOf("20") },
            selectedStructure = remember { mutableStateOf("IT") },
            selectedPersonnel = remember { mutableStateOf(PersonnelListItem(1, "John Doe", "IT")) },
            selectedAccessories = remember { mutableStateOf(emptySet()) },
            availableStructures = listOf("IT", "HR", "Finance"),
            availablePersonnel = listOf(PersonnelListItem(1, "John Doe", "IT")),
            isPersonnelLoading = false,
            personnelError = null,
            onRefreshPersonnel = {},
            onSave = {}
        )
    }
}
