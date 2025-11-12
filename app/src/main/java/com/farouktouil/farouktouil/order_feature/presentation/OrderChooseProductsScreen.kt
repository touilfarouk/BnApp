package com.farouktouil.farouktouil.order_feature.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.order_feature.presentation.components.CheckoutDialog
import com.farouktouil.farouktouil.order_feature.presentation.components.ProductUiListItem
import com.farouktouil.farouktouil.ui.theme.primaryContainerLight
import com.farouktouil.farouktouil.R


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderChooseProductsScreen(
    navController: NavController,
    structureName: String?,
    viewModel: OrderChooseProductsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = structureName) {
        structureName?.takeIf { it.isNotBlank() }?.let { viewModel.initProductList(it) }
            ?: Log.e("OrderChooseProductsScreen", "Structure name is null or blank")
    }

    val productsToShow by viewModel.productsToShow.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isCheckoutDialogShown by viewModel.isCheckoutDialogShown.collectAsStateWithLifecycle()
    val selectedProducts by viewModel.selectedProducts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Section des produits") },
                actions = {
                    IconButton(onClick = { navController.navigate(
                        ScreenRoutes.ProductScreen.route + "/${structureName ?: ""}"
                    ) }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter un produit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCheckoutClick() },
                // containerColor = orange
            ) {
                Icon(
                    imageVector = Icons.Default.Check, // Use the checkmark icon
                    contentDescription = "fab_add_order",
                    // tint = Color.White // Uncomment and use this if you want to set the tint to white
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.background(gray)
                .padding(15.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onProductSearchQueryChange(it) },
                label = { Text(stringResource(id = R.string.order_product_search_label)) },
                colors = TextFieldDefaults.colors(),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                items(
                    items = productsToShow,
                    key = { it.id }
                ) { productListItem ->
                    ProductUiListItem(
                        productListItem = productListItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, primaryContainerLight, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.onListItemClick(productListItem.id) }
                            .padding(10.dp),
                        isExpanded = productListItem.isExpanded,
                        onMinusClick = { viewModel.onMinusClick(productListItem.id) },
                        onPlusClick = { viewModel.onPlusClick(productListItem.id) }
                    )
                }
            }
        }
    }

    if (isCheckoutDialogShown) {
        CheckoutDialog(
            onDismiss = { viewModel.onDismissCheckoutDialog() },
            onConfirm = {
                viewModel.onBuy()
                navController.navigate(ScreenRoutes.OrderScreen.route) {
                    popUpTo(0)
                }
            },
            selectedProducts = selectedProducts
        )
    }
}
