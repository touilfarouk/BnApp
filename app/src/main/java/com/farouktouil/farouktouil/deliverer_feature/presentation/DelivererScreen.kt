package com.farouktouil.farouktouil.deliverer_feature.presentation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar


import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelivererScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    delivererViewModel: DelivererViewModel = hiltViewModel()
) {
    val uiState by delivererViewModel.uiState.collectAsStateWithLifecycle()
    val isAddingDeliverer = remember { mutableStateOf(false) }
    val editingDeliverer = remember { mutableStateOf<Deliverer?>(null) }
    val name = remember { mutableStateOf("") }

    Scaffold(
        topBar =  {
            TopAppBar(
                title = { Text("Deliverer") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isAddingDeliverer.value = true
                editingDeliverer.value = null
                name.value = ""
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Deliverer")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isAddingDeliverer.value) {
                DelivererForm(
                    deliverer = editingDeliverer.value,
                    onSave = { updatedName ->
                        if (editingDeliverer.value != null) {
                            val updatedDeliverer = editingDeliverer.value!!.copy(name = updatedName)
                            delivererViewModel.update(updatedDeliverer)
                        } else {
                            delivererViewModel.insert(updatedName)
                        }
                        isAddingDeliverer.value = false
                    }
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.data) { deliverer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    editingDeliverer.value = deliverer
                                    name.value = deliverer.name
                                    isAddingDeliverer.value = true
                                },

                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = deliverer.name, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    editingDeliverer.value = deliverer
                                    name.value = deliverer.name
                                    isAddingDeliverer.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.Blue
                                    )
                                }
                                IconButton(onClick = { delivererViewModel.delete(deliverer) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
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
fun DelivererForm(deliverer: Deliverer?, onSave: (String) -> Unit) {
    val name = remember { mutableStateOf(deliverer?.name ?: "") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name.value,
            onValueChange = { name.value = it },
            singleLine = true,
            placeholder = { Text(text = "Enter Deliverer Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave.invoke(name.value) }
        ) {
            Text(text = if (deliverer == null) "Save" else "Update")
        }
    }
}
