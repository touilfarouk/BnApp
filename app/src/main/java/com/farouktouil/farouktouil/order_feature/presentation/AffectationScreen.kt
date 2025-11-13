package com.farouktouil.farouktouil.order_feature.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.farouktouil.farouktouil.core.presentation.DrawerSheet
import com.farouktouil.farouktouil.core.presentation.ScreenRoutes
import com.farouktouil.farouktouil.order_feature.presentation.components.OrderDetailDialog
import com.farouktouil.farouktouil.order_feature.presentation.components.OrderUiListItem
import com.farouktouil.farouktouil.ui.theme.primaryLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.farouktouil.farouktouil.R // ← replace with your app's actual package name

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AffectationScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    affectationViewModel: OrderViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        affectationViewModel.refreshOrders()
    }
    fun deleteAffectation(orderId: String) {
        affectationViewModel.deleteOrder(orderId)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(navController, drawerState, scope)
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(ScreenRoutes.OrderChooseStructureScreen.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "fab_add_order"
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Liste des affectations") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (affectationViewModel.orderList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bneder_labs), // replace with your drawable name
                        contentDescription = "No orders illustration",
                        modifier = Modifier
                            .size(880.dp) // adjust size as needed
                            .clip(RoundedCornerShape(24.dp)) // Rounded corners
                            .background(Color.LightGray.copy(alpha = 0.2f)) // Optional subtle background
                            .padding(10.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(affectationViewModel.orderList, key = { it.orderId }) {
                        OrderUiListItem(
                            orderListItem = it,
                            onDeleteClick = { deleteAffectation(it.orderId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, color = primaryLight, RoundedCornerShape(10.dp))
                                .clickable { affectationViewModel.onOrderClick(it.orderId) }
                                .padding(15.dp)
                        )
                    }
                }
            }
        }

        if (affectationViewModel.isOrderDialogShown && affectationViewModel.clickedOrderItem != null) {
            OrderDetailDialog(
                onDismiss = { affectationViewModel.onDismissOrderDialog() },
                orderDetailListItem = affectationViewModel.clickedOrderItem!!
            )
        }
    }
}
