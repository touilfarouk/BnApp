package com.farouktouil.farouktouil.order_feature.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.R
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.order_feature.presentation.components.StructureUiListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderChooseStructureScreen(
    navController: NavController,
    viewModel: OrderChooseStructureViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val structuresToShow by viewModel.structuresToShow.collectAsStateWithLifecycle()
    val structureSearchQuery by viewModel.structureSearchQuery.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Section des commandes") },
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
                value = structureSearchQuery,
                onValueChange = { newQuery -> viewModel.onSearchQueryChange(newQuery) },
                label = { Text("Rechercher une structure") },
                singleLine = true,
                colors = TextFieldDefaults.colors(),
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            if (structuresToShow.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bneder_labs),
                        contentDescription = "No orders illustration",
                        modifier = Modifier
                            .size(880.dp)
                            .padding(0.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            ) {
                items(
                    items = structuresToShow,
                    key = { it.name }
                ) { structureItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encodedName = Uri.encode(structureItem.name)
                                navController.navigate(
                                    ScreenRoutes.OrderChooseProductsScreen.route + "/$encodedName"
                                )
                            },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        StructureUiListItem(
                            structureListItem = structureItem,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }
        }
    }
}
