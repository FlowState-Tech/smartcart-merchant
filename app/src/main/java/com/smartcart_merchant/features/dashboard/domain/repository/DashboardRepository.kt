package com.smartcart_merchant.features.dashboard.domain.repository

import android.net.Uri
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.dashboard.domain.model.BulkUploadResult
import com.smartcart_merchant.features.dashboard.domain.model.CsvPreviewResult
import com.smartcart_merchant.features.dashboard.domain.model.ClearanceResult
import com.smartcart_merchant.features.dashboard.domain.model.InventoryItem
import com.smartcart_merchant.features.dashboard.domain.model.NewProductInput
import com.smartcart_merchant.features.dashboard.domain.model.PagedNotificationHistory
import com.smartcart_merchant.features.dashboard.domain.model.PagedReviews
import com.smartcart_merchant.features.dashboard.domain.model.NotificationPreferences
import com.smartcart_merchant.features.dashboard.domain.model.PriceErrorItem
import com.smartcart_merchant.features.dashboard.domain.model.StoreAnalytics
import com.smartcart_merchant.features.dashboard.domain.model.StoreRating
import com.smartcart_merchant.features.dashboard.domain.model.StoreReview
import com.smartcart_merchant.features.dashboard.domain.model.TrustProfile

interface DashboardRepository {
    suspend fun getAnalytics(storeId: Long): Resource<StoreAnalytics>
    suspend fun getInventory(storeId: Long, categoryId: Long? = null): Resource<List<InventoryItem>>
    suspend fun uploadBulkInventory(storeId: Long, fileUri: Uri, fileName: String): Resource<BulkUploadResult>
    suspend fun previewCsvUpload(fileUri: Uri, fileName: String): Resource<CsvPreviewResult>
    suspend fun addProduct(storeId: Long, input: NewProductInput): Resource<InventoryItem>
    suspend fun updateStock(storeId: Long, item: InventoryItem, quantity: Int): Resource<InventoryItem>
    suspend fun applyClearance(
        storeId: Long,
        item: InventoryItem,
        discountPercentage: Double,
        expiryDate: String,
        reason: String?
    ): Resource<ClearanceResult>
    suspend fun getTrustProfile(storeId: String): Resource<TrustProfile>
    suspend fun getRatings(storeId: String): Resource<List<StoreRating>>
    suspend fun getAllReviews(storeId: String, page: Int = 0, size: Int = 20): Resource<PagedReviews>
    suspend fun replyToReview(storeId: String, reviewId: String, merchantId: String, reply: String): Resource<StoreReview>
    suspend fun getPriceErrors(storeId: String): Resource<List<PriceErrorItem>>
    suspend fun confirmPriceError(storeId: String, errorId: String, confirmed: Boolean): Resource<PriceErrorItem>
    suspend fun getNotificationPreferences(userId: Long): Resource<NotificationPreferences>
    suspend fun updateNotificationPreferences(preferences: NotificationPreferences): Resource<NotificationPreferences>
    suspend fun getNotificationHistory(userId: Long, page: Int = 0, size: Int = 20): Resource<PagedNotificationHistory>
    suspend fun sendTestNotification(userId: Long, channel: String, title: String, body: String): Resource<String>
    suspend fun getCategories(): Resource<List<com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem>>
    suspend fun getCompetitorComparison(storeId: Long, radiusM: Int = 500): Resource<com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison>
    suspend fun getVisibilityRanking(storeId: Long): Resource<com.smartcart_merchant.features.dashboard.domain.model.VisibilityRanking>
    suspend fun updateOperatingHours(storeId: Long, hours: List<com.smartcart_merchant.features.store.domain.model.OperatingHour>): Resource<Unit>
    suspend fun createSupportTicket(storeId: Long, type: String, description: String, lat: Double?, lng: Double?): Resource<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket>
    suspend fun listSupportTickets(storeId: Long): Resource<List<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket>>
    suspend fun submitTicketFeedback(storeId: Long, ticketId: String, score: Int): Resource<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket>
    suspend fun proposeCategory(storeId: Long, name: String, description: String): Resource<String>
}
