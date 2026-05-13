package com.smartcart_merchant.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartcart_merchant.features.catalog.ui.CatalogScreen
import com.smartcart_merchant.features.dashboard.ui.DashboardScreen
import com.smartcart_merchant.features.offer.ui.OffersScreen
import com.smartcart_merchant.features.qr.ui.QrScreen
import com.smartcart_merchant.features.support.ui.SupportScreen

// 1. Definimos las rutas de forma organizada
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Catalog : Screen("catalogo")
    object Ofertas : Screen("ofertas")
    object QR : Screen("qr")
    object Soporte : Screen("soporte")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    // 2. Aquí es donde el NavHost decide qué pantalla mostrar
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route // La pantalla inicial
    ) {
        // Pestaña Dashboard
        composable(Screen.Dashboard.route) {
            // Aquí llamarás a la pantalla que haremos para el Dashboard
            DashboardScreen()
        }

        // Pestaña Catálogo
        composable(Screen.Catalog.route) {
            // Placeholder hasta que hagas el catálogo
            CatalogScreen()
        }

        // Puedes ir agregando las demás conforme las crees
        composable(Screen.Ofertas.route) {
            OffersScreen()
        }
        composable(Screen.QR.route) {
            QrScreen()
        }
        composable(Screen.Soporte.route) {
            SupportScreen()
        }
    }
}