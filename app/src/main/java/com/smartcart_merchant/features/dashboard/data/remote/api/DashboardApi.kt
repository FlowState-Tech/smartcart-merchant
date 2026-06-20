package com.smartcart_merchant.features.dashboard.data.remote.api

import com.smartcart_merchant.features.dashboard.data.remote.dto.BulkUploadResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ConfirmPriceErrorRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.CreateInventoryItemRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.NotificationHistoryPageDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.NotificationStatusResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.PreferenceResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.PriceErrorResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ProductStockResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.RatingResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ReplyReviewRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ReviewResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.StoreAnalyticsResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.TestNotificationRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.TrustProfileResponseDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.UpdatePreferencesRequestDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit client aligned with FlowState-Tech/smartcart-api (GitHub main). Base URL: /api/v1/ */
interface DashboardApi {
    @GET("store-management/stores/{storeId}/analytics")
    suspend fun getAnalytics(@Path("storeId") storeId: Long): Response<StoreAnalyticsResponseDto>

    @GET("store-management/stores/{storeId}/inventory")
    suspend fun getInventory(
        @Path("storeId") storeId: Long,
        @Query("category") category: Long? = null,
        @Query("sku") sku: String? = null
    ): Response<List<ProductStockResponseDto>>

    @Multipart
    @POST("store-management/stores/{storeId}/inventory/bulk")
    suspend fun uploadBulkInventory(
        @Path("storeId") storeId: Long,
        @Part file: MultipartBody.Part
    ): Response<BulkUploadResponseDto>

    @POST("store-management/stores/{storeId}/inventory/items")
    suspend fun addInventoryItem(
        @Path("storeId") storeId: Long,
        @Body request: CreateInventoryItemRequestDto
    ): Response<ProductStockResponseDto>

    @GET("experience/stores/{storeId}/trust-profile")
    suspend fun getTrustProfile(@Path("storeId") storeId: String): Response<TrustProfileResponseDto>

    @GET("experience/stores/{storeId}/ratings")
    suspend fun getRatings(@Path("storeId") storeId: String): Response<List<RatingResponseDto>>

    @GET("experience/stores/{storeId}/reviews/all")
    suspend fun getAllReviews(
        @Path("storeId") storeId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<ReviewResponseDto>>

    @POST("experience/stores/{storeId}/reviews/{reviewId}/reply")
    suspend fun replyToReview(
        @Path("storeId") storeId: String,
        @Path("reviewId") reviewId: String,
        @Body request: ReplyReviewRequestDto
    ): Response<ReviewResponseDto>

    @GET("experience/stores/{storeId}/price-errors")
    suspend fun getPriceErrors(@Path("storeId") storeId: String): Response<List<PriceErrorResponseDto>>

    @PATCH("experience/stores/{storeId}/price-errors/{errorId}/confirm")
    suspend fun confirmPriceError(
        @Path("storeId") storeId: String,
        @Path("errorId") errorId: String,
        @Body request: ConfirmPriceErrorRequestDto
    ): Response<PriceErrorResponseDto>

    @POST("notifications/preferences")
    suspend fun updateNotificationPreferences(
        @Body request: UpdatePreferencesRequestDto
    ): Response<PreferenceResponseDto>

    @GET("notifications/preferences/{userId}")
    suspend fun getNotificationPreferences(
        @Path("userId") userId: Long
    ): Response<PreferenceResponseDto>

    @GET("notifications/history/{userId}")
    suspend fun getNotificationHistory(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<NotificationHistoryPageDto>

    @POST("notifications/send-test")
    suspend fun sendTestNotification(
        @Body request: TestNotificationRequestDto
    ): Response<NotificationStatusResponseDto>
}
