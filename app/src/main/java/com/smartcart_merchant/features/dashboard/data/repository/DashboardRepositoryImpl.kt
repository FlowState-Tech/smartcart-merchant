package com.smartcart_merchant.features.dashboard.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.util.CategoryCatalog
import com.smartcart_merchant.core.util.CsvInventoryNormalizer
import com.smartcart_merchant.features.dashboard.data.remote.api.DashboardApi
import com.smartcart_merchant.features.dashboard.data.remote.dto.ChannelResourceDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ConfirmPriceErrorRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.CreateInventoryItemRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.ReplyReviewRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.SilenceWindowDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.TestNotificationRequestDto
import com.smartcart_merchant.features.dashboard.data.remote.dto.UpdatePreferencesRequestDto
import com.smartcart_merchant.features.dashboard.domain.model.BulkUploadResult
import com.smartcart_merchant.features.dashboard.domain.model.ClearanceResult
import com.smartcart_merchant.features.dashboard.domain.model.CsvPreviewResult
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
import com.smartcart_merchant.features.dashboard.domain.repository.DashboardRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApi,
    @ApplicationContext private val context: Context
) : DashboardRepository {

    companion object {
        private const val TAG = "DashboardRepository"
    }

    override suspend fun getAnalytics(storeId: Long): Resource<StoreAnalytics> {
        return try {
            val response = api.getAnalytics(storeId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.toDomain())
                response.code() == 404 -> Resource.Success(StoreAnalytics())
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAnalytics failed", e)
            Resource.Error(e.message ?: "No se pudieron cargar las métricas")
        }
    }

    override suspend fun getInventory(storeId: Long, categoryId: Long?): Resource<List<InventoryItem>> {
        return try {
            val response = api.getInventory(storeId, categoryId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.map { it.toDomain() })
                response.code() == 404 -> Resource.Success(emptyList())
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getInventory failed", e)
            Resource.Error(e.message ?: "No se pudo cargar el inventario")
        }
    }

    override suspend fun previewCsvUpload(fileUri: Uri, fileName: String): Resource<CsvPreviewResult> {
        return try {
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                val rawBytes = input.readBytes()
                val normalized = try {
                    CsvInventoryNormalizer.normalize(rawBytes, fileName)
                } catch (e: IllegalArgumentException) {
                    return Resource.Error(e.message ?: "Formato CSV inválido")
                }
                Resource.Success(
                    CsvPreviewResult(
                        rowCount = normalized.rowCount,
                        lineErrors = normalized.lineErrors,
                        priceWarnings = normalized.priceWarnings
                    )
                )
            } ?: Resource.Error("No se pudo leer el archivo seleccionado")
        } catch (e: Exception) {
            Log.e(TAG, "previewCsvUpload failed", e)
            Resource.Error(e.message ?: "Error al validar el CSV")
        }
    }

    override suspend fun uploadBulkInventory(
        storeId: Long,
        fileUri: Uri,
        fileName: String
    ): Resource<BulkUploadResult> {
        return try {
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                val rawBytes = input.readBytes()
                val normalized = try {
                    CsvInventoryNormalizer.normalize(rawBytes, fileName)
                } catch (e: IllegalArgumentException) {
                    return Resource.Error(e.message ?: "Formato CSV inválido")
                }

                val requestBody = normalized.bytes.toRequestBody("text/csv".toMediaTypeOrNull())
                val uploadName = fileName.replace(Regex("\\.xlsx?$", RegexOption.IGNORE_CASE), ".csv")
                val part = MultipartBody.Part.createFormData("file", uploadName, requestBody)
                val response = api.uploadBulkInventory(storeId, part)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Resource.Success(
                        BulkUploadResult(
                            jobId = body.jobId,
                            status = body.status.orEmpty(),
                            totalItemsProcessed = body.totalItemsProcessed ?: normalized.rowCount,
                            errorsCount = body.errorsCount ?: 0,
                            lineErrors = normalized.lineErrors.distinct(),
                            priceWarnings = normalized.priceWarnings
                        )
                    )
                } else {
                    Resource.Error(parseError(response.code(), response.errorBody()?.string()))
                }
            } ?: Resource.Error("No se pudo leer el archivo seleccionado")
        } catch (e: Exception) {
            Log.e(TAG, "uploadBulkInventory failed", e)
            Resource.Error(e.message ?: "Error al subir el catálogo")
        }
    }

    override suspend fun addProduct(storeId: Long, input: NewProductInput): Resource<InventoryItem> {
        return upsertItem(
            storeId = storeId,
            request = CreateInventoryItemRequestDto(
                sku = input.sku,
                name = input.name,
                brand = input.brand,
                categoryId = input.categoryId,
                priceAmount = input.priceAmount,
                currency = input.currency,
                quantity = input.quantity,
                minThreshold = input.minThreshold,
                promotional = false,
                discountPercentage = 0.0,
                expiryDate = java.time.LocalDate.now().plusDays(30).toString()
            )
        )
    }

    override suspend fun updateStock(
        storeId: Long,
        item: InventoryItem,
        quantity: Int
    ): Resource<InventoryItem> {
        return upsertItem(
            storeId = storeId,
            request = CreateInventoryItemRequestDto(
                sku = item.sku,
                name = item.name,
                brand = item.brand,
                categoryId = item.categoryId,
                priceAmount = item.priceAmount,
                currency = item.currency,
                quantity = quantity,
                minThreshold = item.minThreshold,
                promotional = item.promotional,
                discountPercentage = 0.0,
                expiryDate = item.expiryDate ?: java.time.LocalDate.now().plusDays(30).toString()
            )
        )
    }

    override suspend fun applyClearance(
        storeId: Long,
        item: InventoryItem,
        discountPercentage: Double,
        expiryDate: String,
        reason: String?
    ): Resource<ClearanceResult> {
        // GitHub main inventory no expone productId; clearance nativo requiere productId numérico.
        // Usamos POST /inventory/items con promotional=true, soportado por el backend.
        return upsertPromotionalItem(storeId, item, discountPercentage, expiryDate)
    }

    private suspend fun upsertPromotionalItem(
        storeId: Long,
        item: InventoryItem,
        discountPercentage: Double,
        expiryDate: String
    ): Resource<ClearanceResult> {
        return try {
            val request = CreateInventoryItemRequestDto(
                sku = item.sku,
                name = item.name,
                brand = item.brand,
                categoryId = item.categoryId,
                priceAmount = item.priceAmount,
                currency = item.currency,
                quantity = item.quantity.coerceAtLeast(1),
                minThreshold = item.minThreshold,
                promotional = true,
                discountPercentage = discountPercentage,
                expiryDate = expiryDate
            )
            val response = api.addInventoryItem(storeId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(
                    ClearanceResult(
                        sku = response.body()!!.sku.orEmpty(),
                        discountPercentage = discountPercentage,
                        expiryDate = expiryDate,
                        status = "APPLIED"
                    )
                )
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "upsertPromotionalItem failed", e)
            Resource.Error(e.message ?: "No se pudo crear la oferta")
        }
    }

    override suspend fun getTrustProfile(storeId: String): Resource<TrustProfile> {
        return try {
            val response = api.getTrustProfile(storeId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.toDomain())
                response.code() == 404 -> Resource.Success(TrustProfile())
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getTrustProfile failed", e)
            Resource.Error(e.message ?: "No se pudo cargar el perfil de confianza")
        }
    }

    override suspend fun getRatings(storeId: String): Resource<List<StoreRating>> {
        return try {
            val response = api.getRatings(storeId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.map { it.toDomain() })
                response.code() == 404 -> Resource.Success(emptyList())
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRatings failed", e)
            Resource.Error(e.message ?: "No se pudieron cargar las calificaciones")
        }
    }

    override suspend fun getAllReviews(storeId: String, page: Int, size: Int): Resource<PagedReviews> {
        return try {
            val response = api.getAllReviews(storeId, page, size)
            when {
                response.isSuccessful && response.body() != null -> {
                    val items = response.body()!!.map { it.toDomain() }
                    Resource.Success(
                        PagedReviews(
                            items = items,
                            page = page,
                            hasMore = items.size >= size
                        )
                    )
                }
                response.code() == 404 -> Resource.Success(PagedReviews(emptyList(), page, false))
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllReviews failed", e)
            Resource.Error(e.message ?: "No se pudieron cargar las reseñas")
        }
    }

    override suspend fun replyToReview(
        storeId: String,
        reviewId: String,
        merchantId: String,
        reply: String
    ): Resource<StoreReview> {
        return try {
            val response = api.replyToReview(
                storeId = storeId,
                reviewId = reviewId,
                request = ReplyReviewRequestDto(merchantId = merchantId, respuesta = reply)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "replyToReview failed", e)
            Resource.Error(e.message ?: "No se pudo responder la reseña")
        }
    }

    override suspend fun getPriceErrors(storeId: String): Resource<List<PriceErrorItem>> {
        return try {
            val response = api.getPriceErrors(storeId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.map { it.toDomain() })
                response.code() == 404 -> Resource.Success(emptyList())
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getPriceErrors failed", e)
            Resource.Error(e.message ?: "No se pudieron cargar los errores de precio")
        }
    }

    override suspend fun confirmPriceError(
        storeId: String,
        errorId: String,
        confirmed: Boolean
    ): Resource<PriceErrorItem> {
        return try {
            val status = if (confirmed) "CONFIRMADO" else "RECHAZADO"
            val response = api.confirmPriceError(
                storeId = storeId,
                errorId = errorId,
                request = ConfirmPriceErrorRequestDto(estado = status)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "confirmPriceError failed", e)
            Resource.Error(e.message ?: "No se pudo actualizar el error de precio")
        }
    }

    override suspend fun getNotificationPreferences(userId: Long): Resource<NotificationPreferences> {
        return try {
            val response = api.getNotificationPreferences(userId)
            when {
                response.isSuccessful && response.body() != null ->
                    Resource.Success(response.body()!!.toDomain())
                response.code() == 404 -> Resource.Success(defaultNotificationPreferences(userId))
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getNotificationPreferences failed", e)
            Resource.Error(e.message ?: "No se pudieron cargar las preferencias")
        }
    }

    override suspend fun updateNotificationPreferences(
        preferences: NotificationPreferences
    ): Resource<NotificationPreferences> {
        return try {
            val request = UpdatePreferencesRequestDto(
                userId = preferences.userId,
                channels = preferences.channels.map {
                    ChannelResourceDto(
                        tipo = it.type,
                        estaHabilitado = it.enabled,
                        tokenContacto = it.contactToken
                    )
                },
                ventanaSilencio = if (!preferences.silenceStart.isNullOrBlank() && !preferences.silenceEnd.isNullOrBlank()) {
                    SilenceWindowDto(preferences.silenceStart, preferences.silenceEnd)
                } else {
                    null
                }
            )
            val response = api.updateNotificationPreferences(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateNotificationPreferences failed", e)
            Resource.Error(e.message ?: "No se pudieron guardar las preferencias")
        }
    }

    override suspend fun getNotificationHistory(
        userId: Long,
        page: Int,
        size: Int
    ): Resource<PagedNotificationHistory> {
        return try {
            val response = api.getNotificationHistory(userId, page, size)
            when {
                response.isSuccessful && response.body() != null -> {
                    val body = response.body()!!
                    val items = body.content.orEmpty().map { it.toDomain() }
                    val total = body.totalElements ?: items.size.toLong()
                    val totalPages = body.totalPages ?: 0
                    val currentPage = body.number ?: page
                    Resource.Success(
                        PagedNotificationHistory(
                            items = items,
                            page = currentPage,
                            totalElements = total,
                            hasMore = currentPage + 1 < totalPages
                        )
                    )
                }
                response.code() == 404 -> Resource.Success(
                    PagedNotificationHistory(emptyList(), page, 0, false)
                )
                else -> Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getNotificationHistory failed", e)
            Resource.Error(e.message ?: "No se pudo cargar el historial")
        }
    }

    override suspend fun sendTestNotification(
        userId: Long,
        channel: String,
        title: String,
        body: String
    ): Resource<String> {
        return try {
            val response = api.sendTestNotification(
                TestNotificationRequestDto(
                    userId = userId,
                    channel = channel,
                    titulo = title,
                    cuerpo = body
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                val status = body?.estado ?: "ENVIADO"
                val channel = body?.canal?.let { " ($it)" }.orEmpty()
                Resource.Success("$status$channel")
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "sendTestNotification failed", e)
            Resource.Error(e.message ?: "No se pudo enviar la notificación de prueba")
        }
    }

    override suspend fun getCategories(): Resource<List<com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem>> {
        return Resource.Success(
            CategoryCatalog.defaults.map {
                com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem(
                    id = it.id,
                    name = it.name,
                    description = it.description
                )
            }
        )
    }

    override suspend fun getCompetitorComparison(
        storeId: Long,
        radiusM: Int
    ): Resource<com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison> {
        return Resource.Error("Comparador de cadena no disponible en el backend actual (FlowState-Tech/smartcart-api main)")
    }

    override suspend fun getVisibilityRanking(
        storeId: Long
    ): Resource<com.smartcart_merchant.features.dashboard.domain.model.VisibilityRanking> {
        return Resource.Error("Ranking de visibilidad no disponible en el backend actual (FlowState-Tech/smartcart-api main)")
    }

    override suspend fun updateOperatingHours(
        storeId: Long,
        hours: List<com.smartcart_merchant.features.store.domain.model.OperatingHour>
    ): Resource<Unit> {
        // PATCH operating-hours no existe en GitHub main; persistencia local vía SessionPreferences.
        return Resource.Success(Unit)
    }

    override suspend fun createSupportTicket(
        storeId: Long,
        type: String,
        description: String,
        lat: Double?,
        lng: Double?
    ): Resource<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket> {
        return Resource.Error("Tickets de soporte no disponibles en el backend. Usa soporte@flowstatetech.com")
    }

    override suspend fun listSupportTickets(
        storeId: Long
    ): Resource<List<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket>> {
        return Resource.Success(emptyList())
    }

    override suspend fun submitTicketFeedback(
        storeId: Long,
        ticketId: String,
        score: Int
    ): Resource<com.smartcart_merchant.features.dashboard.domain.model.SupportTicket> {
        return Resource.Error("Feedback de tickets no disponible en el backend actual")
    }

    override suspend fun proposeCategory(
        storeId: Long,
        name: String,
        description: String
    ): Resource<String> {
        return Resource.Error("Propuesta de categorías no disponible en el backend actual. Usa categoryId 1-8 del catálogo estándar.")
    }

    private suspend fun upsertItem(
        storeId: Long,
        request: CreateInventoryItemRequestDto
    ): Resource<InventoryItem> {
        return try {
            val response = api.addInventoryItem(storeId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(parseError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "upsertItem failed", e)
            Resource.Error(e.message ?: "No se pudo actualizar el producto")
        }
    }

    private fun defaultNotificationPreferences(userId: Long): NotificationPreferences {
        return NotificationPreferences(
            userId = userId,
            channels = listOf(
                com.smartcart_merchant.features.dashboard.domain.model.NotificationChannelPref("PUSH", true, null),
                com.smartcart_merchant.features.dashboard.domain.model.NotificationChannelPref("EMAIL", true, null),
                com.smartcart_merchant.features.dashboard.domain.model.NotificationChannelPref("SMS", false, null)
            )
        )
    }

    private fun parseError(code: Int, body: String?): String {
        return when {
            !body.isNullOrBlank() -> "Error $code: $body"
            else -> "Error $code al comunicarse con el servidor"
        }
    }
}
