package com.farouktouil.farouktouil.order_feature.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.order_feature.presentation.components.DelivererUiListItem
import com.farouktouil.farouktouil.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderChooseDelivererScreen(
    navController: NavController,
    orderChooseDelivererViewModel: OrderChooseDelivererViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val deliverersToShow by orderChooseDelivererViewModel.deliverersToShow.collectAsStateWithLifecycle()
    val delivererSearchQuery by orderChooseDelivererViewModel.delivererSearchQuery.collectAsStateWithLifecycle()
    val isLoading by orderChooseDelivererViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by orderChooseDelivererViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    /*    floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ScreenRoutes.DelivererScreen.route) },
                // containerColor = orange
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Deliverer",
                   // tint = white
                )
            }
        },*/
        topBar = {
            TopAppBar(
                title = { Text("Order Overview", ) },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(15.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = delivererSearchQuery,
                onValueChange = { newQuery -> orderChooseDelivererViewModel.onSearchQueryChange(newQuery) },
                label = { Text("Search Deliverer") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    //focusedBorderColor = orange,
                  //  cursorColor = orange,
                   // textColor = gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            if (deliverersToShow.isEmpty() && !isLoading) {
                Text("No deliverers found", modifier = Modifier.padding(16.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            ) {
                items(
                    items = deliverersToShow,
                    key = { it.delivererId }
                ) { delivererListItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    ScreenRoutes.OrderChooseProductsScreen.route + "/${delivererListItem.delivererId}"
                                )
                            },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        DelivererUiListItem(
                            delivererListItem = delivererListItem,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }
        }
    }
}
