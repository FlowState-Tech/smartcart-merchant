package com.smartcart_merchant.features.dashboard.domain.model

import com.smartcart_merchant.features.dashboard.presentation.state.StockStatus

data class StoreAnalytics(
    val totalViews: Long = 0,
    val abandonedCarts: Int = 0,
    val conversionRate: Double = 0.0,
    val topProducts: List<String> = emptyList()
)

data class InventoryItem(
    val productId: Long? = null,
    val sku: String,
    val name: String,
    val brand: String = "Generic",
    val categoryId: Long = 1L,
    val priceAmount: Double = 0.0,
    val currency: String = "PEN",
    val price: String,
    val stockStatus: StockStatus,
    val active: Boolean = true,
    val promotional: Boolean = false,
    val quantity: Int = 0,
    val minThreshold: Int = 5,
    val expiryDate: String? = null,
    val secondaryCategoryIds: List<Long> = emptyList()
)

data class TrustProfile(
    val trustScore: Double = 0.0,
    val totalRatings: Int = 0,
    val confirmedPriceErrors: Int = 0,
    val badges: List<String> = emptyList()
)

data class StoreRating(
    val ratingId: String,
    val score: Int,
    val registeredAt: String
)

data class PriceErrorItem(
    val errorId: String,
    val productId: String,
    val discrepancy: Double,
    val status: String,
    val reportedAt: String
)

data class StoreReview(
    val reviewId: String,
    val comment: String,
    val status: String,
    val reply: String?,
    val createdAt: String
)

data class NotificationChannelPref(
    val type: String,
    val enabled: Boolean,
    val contactToken: String?
)

data class NotificationPreferences(
    val userId: Long = 0,
    val channels: List<NotificationChannelPref> = emptyList(),
    val silenceStart: String? = null,
    val silenceEnd: String? = null
)

data class NotificationHistoryItem(
    val id: Long,
    val channel: String,
    val status: String,
    val subject: String,
    val summary: String,
    val createdAt: String
)

data class NewProductInput(
    val sku: String,
    val name: String,
    val brand: String,
    val categoryId: Long,
    val priceAmount: Double,
    val currency: String,
    val quantity: Int,
    val minThreshold: Int
)

data class BulkUploadResult(
    val jobId: String?,
    val status: String,
    val totalItemsProcessed: Int,
    val errorsCount: Int,
    val lineErrors: List<String> = emptyList(),
    val priceWarnings: List<String> = emptyList()
)

data class CsvPreviewResult(
    val rowCount: Int,
    val lineErrors: List<String> = emptyList(),
    val priceWarnings: List<String> = emptyList()
)

data class PagedReviews(
    val items: List<StoreReview>,
    val page: Int,
    val hasMore: Boolean
)

data class PagedNotificationHistory(
    val items: List<NotificationHistoryItem>,
    val page: Int,
    val totalElements: Long,
    val hasMore: Boolean
)

data class RetailCategoryItem(
    val id: Long,
    val name: String,
    val description: String
)

data class CompetitorComparison(
    val storeId: Long,
    val radiusMeters: Int,
    val storeAveragePrice: Double,
    val zoneAveragePrice: Double,
    val savingsLeader: Boolean,
    val leaderBadge: String,
    val lastUpdated: String,
    val competitors: List<CompetitorPriceEntry>
)

data class CompetitorPriceEntry(
    val competitorName: String,
    val sku: String,
    val productName: String,
    val competitorPrice: Double,
    val storePrice: Double,
    val difference: Double
)

data class VisibilityRanking(
    val storeId: Long,
    val district: String,
    val rank: Int,
    val totalStoresInDistrict: Int,
    val cheapestInDistrict: Boolean,
    val message: String,
    val evaluatedAt: String
)

data class SupportTicket(
    val ticketId: String,
    val storeId: Long,
    val type: String,
    val description: String,
    val status: String,
    val createdAt: String,
    val resolvedAt: String?
)

data class SeasonalCampaign(
    val categoryId: Long,
    val discountPercentage: Double,
    val expiryDate: String,
    val pointsCost: Int = 100,
    val active: Boolean = true
)

data class UploadJobState(
    val fileName: String,
    val storeId: Long,
    val jobId: String?,
    val processedCount: Int,
    val timestamp: String
)

data class ClearanceResult(
    val sku: String,
    val discountPercentage: Double,
    val expiryDate: String,
    val status: String
)
