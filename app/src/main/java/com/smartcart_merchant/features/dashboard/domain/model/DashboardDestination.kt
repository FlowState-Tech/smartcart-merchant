package com.smartcart_merchant.features.dashboard.domain.model

enum class DashboardDestination(
    val title: String
) {
    DASHBOARD("Dashboard"),
    CATALOG_MANAGEMENT("Gestión de Catálogo"),
    OFFER_MANAGEMENT("Gestión de Ofertas"),
    STORE_QR("Código QR de la tienda"),
    SUPPORT("Soporte")
}
