package com.farouktouil.farouktouil.core.presentation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.farouktouil.farouktouil.barcode_feature.presentation.BarcodeGeneratorScreen
import com.farouktouil.farouktouil.consultation_feature.presentation.ConsultationScreen
import com.farouktouil.farouktouil.news_feature.presentation.NewsScreen
import com.farouktouil.farouktouil.order_feature.presentation.AffectationScreen
import com.farouktouil.farouktouil.order_feature.presentation.OrderChooseProductsScreen
import com.farouktouil.farouktouil.order_feature.presentation.OrderChooseStructureScreen
import com.farouktouil.farouktouil.personnel_feature.presentation.PersonnelScreen
import com.farouktouil.farouktouil.product_feature.presentation.BarcodeScannerScreen
import com.farouktouil.farouktouil.product_feature.presentation.ScanProductScreen
import com.farouktouil.farouktouil.product_feature.presentation.ProductDetailsScreen
import com.farouktouil.farouktouil.product_feature.presentation.ProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxHeight(),
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(navController, drawerState, scope)
        },
        gesturesEnabled = true // Ensuring proper swipe behavior
    ) {
        NavHost(
            navController,
            startDestination = ScreenRoutes.AffectationScreen.route
        ) {
            composable(ScreenRoutes.AffectationScreen.route) {
                AffectationScreen(navController = navController, drawerState = drawerState, scope = scope)
            }
            composable(ScreenRoutes.ProductScreen.route) {
                ProductScreen(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    structureNameArg = null
                )
            }
            composable(
                route = ScreenRoutes.ProductScreen.route + "/{structureName}",
                arguments = listOf(navArgument("structureName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val structureName = backStackEntry.arguments?.getString("structureName")
                ProductScreen(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    structureNameArg = structureName
                )
            }

            composable(ScreenRoutes.OrderChooseStructureScreen.route) {
                OrderChooseStructureScreen(navController = navController)
            }
            composable(
                ScreenRoutes.OrderChooseProductsScreen.route + "?structureName={structureName}",
                arguments = listOf(
                    navArgument("structureName") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val structureName = backStackEntry.arguments?.getString("structureName")
                OrderChooseProductsScreen(
                    navController = navController,
                    structureName = structureName
                )
            }

            composable(ScreenRoutes.PersonnelScreen.route) {
                PersonnelScreen(
                    drawerState = drawerState,
                    scope = scope
                )
            }

            composable(ScreenRoutes.AppelConsultationScreen.route) {
                ConsultationScreen(
                    drawerState = drawerState,
                    scope = scope
                )
            }

            composable(ScreenRoutes.NewsScreen.route) {
                NewsScreen(
                    drawerState = drawerState,
                    scope = scope
                )
            }

            composable(ScreenRoutes.BarcodeGeneratorScreen.route) {
                BarcodeGeneratorScreen(drawerState = drawerState, scope = scope)
            }

            composable(ScreenRoutes.BarcodeScannerScreen.route) {
                BarcodeScannerScreen(
                    onBarcodeScanned = { barcode ->
                        navController.navigate(ScreenRoutes.ProductDetailsScreen.route + "?barcode=$barcode")
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }

            composable(ScreenRoutes.ScanProductScreen.route) {
                ScanProductScreen(
                    onOpenDetails = { value ->
                        // If the scanned value is a numeric productId, navigate using productId query param
                        val asInt = value.toIntOrNull()
                        if (asInt != null) {
                            navController.navigate(ScreenRoutes.ProductDetailsScreen.route + "?productId=$asInt")
                        } else {
                            navController.navigate(ScreenRoutes.ProductDetailsScreen.route + "?barcode=$value")
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ScreenRoutes.ProductDetailsScreen.route + "?productId={productId}&barcode={barcode}",
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument("barcode") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId")?.takeIf { it != -1 }
                val barcode = backStackEntry.arguments?.getString("barcode")?.takeIf { it.isNotEmpty() }

                ProductDetailsScreen(
                    productId = productId,
                    barcode = barcode,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

sealed class ScreenRoutes(val route: String) {
    object AffectationScreen : ScreenRoutes("affectation_screen")
    object OrderChooseStructureScreen:ScreenRoutes("order_choose_structure_screen")
    object OrderChooseProductsScreen:ScreenRoutes("order_choose_products_screen")
    object ProductScreen:ScreenRoutes("product_screen")
    object ContactScreen:ScreenRoutes("contact_screen")
    object PersonnelScreen:ScreenRoutes("personnel_screen")
    object AppelConsultationScreen:ScreenRoutes("appel_consultation_screen")
    object NewsScreen:ScreenRoutes("news_screen")
    object BarcodeGeneratorScreen:ScreenRoutes("barcode_generator_screen")
    object BarcodeScannerScreen:ScreenRoutes("barcode_scanner_screen")
    object ScanProductScreen:ScreenRoutes("scan_product_screen")
    object ProductDetailsScreen:ScreenRoutes("product_details_screen")
}