package com.smartcart_merchant.features.dashboard.presentation.state

import android.net.Uri
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination
import com.smartcart_merchant.features.dashboard.domain.model.InventoryItem
import com.smartcart_merchant.features.dashboard.domain.model.NotificationHistoryItem
import com.smartcart_merchant.features.dashboard.domain.model.NotificationPreferences
import com.smartcart_merchant.features.dashboard.domain.model.PriceErrorItem
import com.smartcart_merchant.features.dashboard.domain.model.StoreRating
import com.smartcart_merchant.features.dashboard.domain.model.StoreReview
import com.smartcart_merchant.features.dashboard.domain.model.TrustProfile
import com.smartcart_merchant.features.store.domain.model.OperatingHour

data class DashboardUiState(
    val selectedDestination: DashboardDestination = DashboardDestination.DASHBOARD,
    val companyName: String = "Mi Tienda",
    val ruc: String = "",
    val merchantId: String = "",
    val storeId: String = "",
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val isCreatingOffer: Boolean = false,
    val metrics: List<DashboardMetric> = emptyList(),
    val topDemandProducts: List<String> = emptyList(),
    val demandAlerts: List<DemandAlert> = emptyList(),
    val competitorComparison: com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison? = null,
    val visibilityRanking: com.smartcart_merchant.features.dashboard.domain.model.VisibilityRanking? = null,
    val competitorRadiusM: Int = 500,
    val visibilityAlertsEnabled: Boolean = true,
    val storeHours: List<OperatingHour> = emptyList(),
    val storeOpenStatus: String = "Desconocido",
    val is24Hours: Boolean = false,
    val categories: List<com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem> = emptyList(),
    val supportTickets: List<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket> = emptyList(),
    val weeklyOfferSuggestion: String? = null,
    val lastUploadSummary: String? = null,
    val catalogProducts: List<CatalogProduct> = emptyList(),
    val inventoryItems: List<InventoryItem> = emptyList(),
    val activeOffers: List<ActiveOffer> = emptyList(),
    val trustProfile: TrustProfile = TrustProfile(),
    val averageRating: Double = 0.0,
    val ratings: List<StoreRating> = emptyList(),
    val reviews: List<StoreReview> = emptyList(),
    val reviewsPage: Int = 0,
    val reviewsHasMore: Boolean = false,
    val isLoadingMoreReviews: Boolean = false,
    val priceErrors: List<PriceErrorItem> = emptyList(),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val notificationHistory: List<NotificationHistoryItem> = emptyList(),
    val notificationsPage: Int = 0,
    val notificationsHasMore: Boolean = false,
    val isLoadingMoreNotifications: Boolean = false,
    val isSavingNotifications: Boolean = false,
    val isSendingTestNotification: Boolean = false,
    val selectedCategoryFilter: Long? = null,
    val stockSnapshots: Map<String, Int> = emptyMap(),
    val offerDiscounts: Map<String, Double> = emptyMap(),
    val lastUploadLineErrors: List<String> = emptyList(),
    val pendingCsvUpload: PendingCsvUpload? = null,
    val showAddProductDialog: Boolean = false,
    val qrLandingUrl: String = "",
    val qrVisitCount: Long = 0,
    val snackbarMessage: String? = null,
    val error: String? = null,
    val showOfferDialog: Boolean = false,
    val showBulkOfferDialog: Boolean = false,
    val showHoursEditor: Boolean = false
)

data class PendingCsvUpload(
    val uri: Uri,
    val fileName: String,
    val rowCount: Int,
    val lineErrors: List<String>,
    val priceWarnings: List<String>
)

data class DemandAlert(
    val productName: String,
    val sku: String,
    val message: String
)

data class DashboardMetric(
    val label: String,
    val value: String,
    val trend: String
)

data class CatalogProduct(
    val sku: String,
    val name: String,
    val price: String,
    val categoryId: Long = 1L,
    val categoryName: String = "",
    val stockStatus: StockStatus,
    val promotional: Boolean = false
)

enum class StockStatus(val label: String) {
    IN_STOCK("En stock"),
    LOW_STOCK("Stock bajo"),
    OUT_OF_STOCK("Sin stock")
}

data class ActiveOffer(
    val sku: String,
    val productName: String,
    val discount: String,
    val expiresIn: String,
    val isFlashSale: Boolean,
    val categoryId: Long = 1L
)

data class SupportTopic(
    val question: String,
    val answer: String
)
