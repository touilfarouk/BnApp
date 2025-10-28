package com.farouktouil.farouktouil.core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.farouktouil.farouktouil.R
import com.farouktouil.farouktouil.core.util.AppConstant.APP_NAME
import com.farouktouil.farouktouil.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DrawerSheet(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(surfaceLight) // Solid background color
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        // Clean header section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp, start = 16.dp, end = 16.dp), // moved down by 32.dp
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = primaryLight,
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = APP_NAME,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Navigation menu items
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Orders
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_orders), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScreenRoutes.OrderScreen.route) {
                            popUpTo(ScreenRoutes.OrderScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Deliverers
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_deliverers), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScreenRoutes.DelivererScreen.route) {
                            popUpTo(ScreenRoutes.DelivererScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Products
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_products), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Navigate to ProductScreen without deliverer filter to show all products
                        navController.navigate(ScreenRoutes.ProductScreen.route) {
                            popUpTo(ScreenRoutes.ProductScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Personnel
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_personnel), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScreenRoutes.PersonnelScreen.route) {
                            popUpTo(ScreenRoutes.PersonnelScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Appel à Consultation
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_appel_consultation), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScreenRoutes.AppelConsultationScreen.route) {
                            popUpTo(ScreenRoutes.AppelConsultationScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Barcode Generator
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_barcode_generator), style = MaterialTheme.typography.titleMedium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScreenRoutes.BarcodeGeneratorScreen.route) {
                            popUpTo(ScreenRoutes.BarcodeGeneratorScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App version or additional info (optional)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.app_version),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
