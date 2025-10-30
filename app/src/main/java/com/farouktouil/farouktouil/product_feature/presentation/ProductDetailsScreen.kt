package com.farouktouil.farouktouil.product_feature.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.farouktouil.farouktouil.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Int? = null,
    barcode: String? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ProductViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId, barcode) {
        isLoading = true
        errorMessage = null

        try {
            product = when {
                productId != null -> {
                    withContext(Dispatchers.IO) {
                        viewModel.getProductById(productId)
                    }
                }
                barcode != null -> {
                    withContext(Dispatchers.IO) {
                        viewModel.getProductByBarcode(barcode)
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement du produit : ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.product_details)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = {
                            scope.launch {
                                // TODO: Implémenter la fonction d’édition
                            }
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Modifier le produit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = "Erreur",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text(stringResource(id = R.string.go_back))
                        }
                    }
                }
                product != null -> {
                    ProductDetailsContent(
                        product = product!!,
                        onUpdateQuantity = { newQuantity ->
                            scope.launch {
                                viewModel.updateProductQuantity(product!!.productId, newQuantity)
                            }
                        }
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Inventory,
                            contentDescription = "Aucun produit",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Produit introuvable",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Le code-barres scanné ne correspond à aucun produit dans la base de données.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text(stringResource(id = R.string.scan_another_product))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: ProductEntity,
    onUpdateQuantity: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Carte d’en-tête du produit
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Inventory,
                        contentDescription = "Produit",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (product.label.isNotEmpty()) {
                            Text(
                                text = product.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (product.barcode.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.QrCode,
                            contentDescription = "Code-barres",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Code-barres : ${product.barcode}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "ID du produit : ${product.productId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Informations sur le stock
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Informations sur le stock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quantité actuelle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantité actuelle :",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${product.quantity}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            product.quantity <= product.minQuantity -> Color.Red
                            product.quantity >= product.maxQuantity -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantité minimale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantité minimale :",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${product.minQuantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantité maximale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantité maximale :",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${product.maxQuantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // État du stock
                val stockStatus = when {
                    product.quantity <= product.minQuantity -> "Stock faible"
                    product.quantity >= product.maxQuantity -> "Surstock"
                    else -> "Stock normal"
                }

                val statusColor = when (stockStatus) {
                    "Stock faible" -> Color.Red
                    "Surstock" -> Color(0xFFFF9800)
                    else -> Color.Green
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "État du stock :",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stockStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Informations sur le prix
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Informations sur le prix",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Prix unitaire :",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${String.format("%.2f", product.pricePerAmount)} $",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val totalValue = product.quantity * product.pricePerAmount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Valeur totale :",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${String.format("%.2f", totalValue)} $",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Informations sur le Structures
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Informations sur le Structures",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ID du Structures : ${product.belongsToDeliverer}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Remarque : les détails complets du Structures seront affichés ici",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Boutons d’action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: Implémenter l’ajustement du stock */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Ajuster la quantité")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajuster le stock")
            }

            Button(
                onClick = { /* TODO: Implémenter la commande */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = "Commander")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Commander plus")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
