package com.smartcart_merchant.core.designsystem.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcart_merchant.core.navigation.AppNavigation
import com.smartcart_merchant.core.navigation.Screen

// ==========================================================
// 1. MODELOS DE DATOS (Para coherencia con la arquitectura)
// ==========================================================

data class UserUi(
    val name: String,
    val role: String,
    val avatarResId: Int? = null
)

data class DrawerSection(
    val headerText: String,
    val items: List<DrawerDestination>
)

// Definimos los destinos que aparecen en tu mockup
sealed class DrawerDestination(val id: String, val label: String) {
    object Dashboard : DrawerDestination("dashboard", "Dashboard")
    object Catalogo : DrawerDestination("catalogo", "Catálogo")
    object Ofertas : DrawerDestination("ofertas", "Gestión de Ofertas")
    object QR : DrawerDestination("qr", "Código QR")
    object Soporte : DrawerDestination("soporte", "Soporte")
}

// ==========================================================
// 2. COMPONENTE PRINCIPAL (MainLayout)
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    title: String,
    user: UserUi,
    appName: String = "SmartCart",
    drawerSections: List<DrawerSection>,
    selectedDestinationId: String,
    onDestinationClick: (DrawerDestination) -> Unit,
    content: @Composable () -> Unit
) {
    PermanentNavigationDrawer(
        drawerContent = {
            // Este es el panel lateral fijo para la S6 Lite
            PermanentDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Nombre de la App
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Secciones del Menú
                    drawerSections.forEach { section ->
                        Text(
                            text = section.headerText,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        section.items.forEach { item ->
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = item.id == selectedDestinationId,
                                onClick = { onDestinationClick(item) },
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Barra superior con información del usuario
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    actions = {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(user.name, style = MaterialTheme.typography.bodyMedium)
                            Text(user.role, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Espacio de contenido dinámico
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                content() // Aquí el NavHost "pintará" la pestaña activa
            }
        }
    }
}



@Preview(device = "spec:width=1280dp,height=800dp,orientation=landscape", showBackground = true)
@Composable
fun MainLayoutNavigationPreview() {
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

    MaterialTheme {
        MainLayout(
            title = "Dashboard General",
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
