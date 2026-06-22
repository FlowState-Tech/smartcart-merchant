package com.smartcart_merchant.features.dashboard.domain.model


import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcart_merchant.R

enum class DashboardDestination(
    val title: String,
    val icon: Int,
    val contentDescription: String
) {
    DASHBOARD(
        title = "Dashboard",
        icon = R.drawable.dashboard,
        contentDescription = "Panel principal"
    ),
    CATALOG_MANAGEMENT(
        title = "Gestión de Catálogo",
        icon = R.drawable.inventory,
        contentDescription = "Gestión de catálogo"
    ),
    OFFER_MANAGEMENT(
        title = "Gestión de Ofertas",
        icon = R.drawable.percent_discount,
        contentDescription = "Gestión de ofertas"
    ),

    STORE_QR(
        title = "Código QR de la tienda",
        icon = R.drawable.qr_code,
        contentDescription = "Código QR de la tienda"
    ),
    REVIEWS(
        title = "Reseñas",
        icon = R.drawable.rate_review,
        contentDescription = "Gestión de reseñas"
    ),
    NOTIFICATIONS(
        title = "Notificaciones",
        icon = R.drawable.notifications,
        contentDescription = "Preferencias de notificaciones"
    ),
    SUPPORT(
        title = "Soporte",
        icon = R.drawable.support_agent,
        contentDescription = "Soporte"
    );

    companion object {
        val logoutIcon = R.drawable.logout
    }
}
