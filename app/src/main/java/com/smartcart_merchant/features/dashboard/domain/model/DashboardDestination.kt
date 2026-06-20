package com.smartcart_merchant.features.dashboard.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

enum class DashboardDestination(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    DASHBOARD(
        title = "Dashboard",
        icon = Icons.Outlined.Dashboard,
        contentDescription = "Panel principal"
    ),
    CATALOG_MANAGEMENT(
        title = "Gestión de Catálogo",
        icon = Icons.Outlined.Inventory2,
        contentDescription = "Gestión de catálogo"
    ),
    OFFER_MANAGEMENT(
        title = "Gestión de Ofertas",
        icon = Icons.Outlined.LocalOffer,
        contentDescription = "Gestión de ofertas"
    ),
    STORE_QR(
        title = "Código QR de la tienda",
        icon = Icons.Outlined.QrCode2,
        contentDescription = "Código QR de la tienda"
    ),
    REVIEWS(
        title = "Reseñas",
        icon = Icons.Outlined.RateReview,
        contentDescription = "Gestión de reseñas"
    ),
    NOTIFICATIONS(
        title = "Notificaciones",
        icon = Icons.Outlined.Notifications,
        contentDescription = "Preferencias de notificaciones"
    ),
    SUPPORT(
        title = "Soporte",
        icon = Icons.Outlined.SupportAgent,
        contentDescription = "Soporte"
    );

    companion object {
        val logoutIcon = Icons.AutoMirrored.Outlined.Logout
    }
}
