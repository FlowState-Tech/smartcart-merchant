package com.smartcart_merchant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcart_merchant.core.designsystem.layout.DrawerDestination
import com.smartcart_merchant.core.designsystem.layout.DrawerSection
import com.smartcart_merchant.core.designsystem.layout.MainLayout
import com.smartcart_merchant.core.designsystem.layout.UserUi
import com.smartcart_merchant.core.navigation.AppNavigation
import com.smartcart_merchant.core.navigation.Screen
import com.smartcart_merchant.ui.theme.SmartcartmerchantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartcartmerchantTheme {
                val sections = listOf(
                    DrawerSection(
                        headerText = "Principal",
                        items = listOf(DrawerDestination.Dashboard)
                    ),
                    DrawerSection(
                        headerText = "Inventario",
                        items = listOf(DrawerDestination.Catalogo, DrawerDestination.Ofertas)
                    ),
                    DrawerSection(
                        headerText = "Herramientas",
                        items = listOf(DrawerDestination.QR, DrawerDestination.Soporte)
                    )
                )

                val navController = rememberNavController()
                val backStackEntry = navController.currentBackStackEntryAsState().value
                val currentRoute = backStackEntry?.destination?.route ?: Screen.Dashboard.route
                val screenTitle = when (currentRoute) {
                    Screen.Dashboard.route -> "Dashboard"
                    Screen.Catalog.route -> "Gestion de Catalogo"
                    Screen.Ofertas.route -> "Gestion de Ofertas"
                    Screen.QR.route -> "Codigo QR"
                    Screen.Soporte.route -> "Soporte Tecnico"
                    else -> "SmartCart"
                }

                MainLayout(
                    title = screenTitle,
                    user = UserUi("Comerciante", "Administrador"),
                    appName = "SmartCart",
                    drawerSections = sections,
                    selectedDestinationId = currentRoute,
                    onDestinationClick = { destination ->
                        val route = when (destination) {
                            DrawerDestination.Dashboard -> Screen.Dashboard.route
                            DrawerDestination.Catalogo -> Screen.Catalog.route
                            DrawerDestination.Ofertas -> Screen.Ofertas.route
                            DrawerDestination.QR -> Screen.QR.route
                            DrawerDestination.Soporte -> Screen.Soporte.route
                        }
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartcartmerchantTheme {
        Greeting("Android")
    }
}