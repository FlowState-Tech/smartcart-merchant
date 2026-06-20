package com.smartcart_merchant.features.dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.smartcart_merchant.features.dashboard.domain.model.InventoryItem
import com.smartcart_merchant.features.dashboard.domain.model.StoreAnalytics
import com.smartcart_merchant.features.dashboard.presentation.state.StockStatus

data class MetricsDto(
    @SerializedName("totalViews") val totalViews: Long? = null,
    @SerializedName("abandonedCarts") val abandonedCarts: Int? = null,
    @SerializedName("conversionRate") val conversionRate: Double? = null,
    @SerializedName("topProducts") val topProducts: List<String>? = null
)

data class StoreAnalyticsResponseDto(
    @SerializedName("storeId") val storeId: Long? = null,
    @SerializedName("metrics") val metrics: MetricsDto? = null
) {
    fun toDomain(): StoreAnalytics {
        val m = metrics
        return StoreAnalytics(
            totalViews = m?.totalViews ?: 0,
            abandonedCarts = m?.abandonedCarts ?: 0,
            conversionRate = m?.conversionRate ?: 0.0,
            topProducts = m?.topProducts.orEmpty()
        )
    }
}

data class ProductStockResponseDto(
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("categoryId") val categoryId: Long? = null,
    @SerializedName("active") val active: Boolean? = null,
    @SerializedName("priceAmount") val priceAmount: Double? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("promotional") val promotional: Boolean? = null,
    @SerializedName("expiryDate") val expiryDate: String? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("minThreshold") val minThreshold: Int? = null,
    @SerializedName("productId") val productId: Long? = null
) {
    fun toDomain(): InventoryItem {
        val qty = quantity ?: 0
        val threshold = minThreshold ?: 0
        val isActive = active != false
        val status = when {
            !isActive || qty <= 0 -> StockStatus.OUT_OF_STOCK
            qty <= threshold -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
        val currencyLabel = currency?.takeIf { it.isNotBlank() } ?: "PEN"
        val price = priceAmount?.let { amount ->
            if (currencyLabel.equals("PEN", ignoreCase = true)) {
                "S/ ${"%.2f".format(amount)}"
            } else {
                "$currencyLabel ${"%.2f".format(amount)}"
            }
        } ?: "-"

        return InventoryItem(
            productId = productId,
            sku = sku.orEmpty(),
            name = name.orEmpty(),
            brand = brand?.takeIf { it.isNotBlank() } ?: "Generic",
            categoryId = categoryId ?: 1L,
            priceAmount = priceAmount ?: 0.0,
            currency = currencyLabel,
            price = price,
            stockStatus = status,
            active = isActive,
            promotional = promotional == true,
            quantity = qty,
            minThreshold = threshold,
            expiryDate = expiryDate
        )
    }
}

data class CreateInventoryItemRequestDto(
    @SerializedName("sku") val sku: String,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("priceAmount") val priceAmount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("minThreshold") val minThreshold: Int,
    @SerializedName("promotional") val promotional: Boolean,
    @SerializedName("discountPercentage") val discountPercentage: Double,
    @SerializedName("expiryDate") val expiryDate: String
)

data class TrustProfileResponseDto(
    @SerializedName("storeId") val storeId: String? = null,
    @SerializedName("trustScore") val trustScore: Double? = null,
    @SerializedName("totalCalificaciones") val totalCalificaciones: Int? = null,
    @SerializedName("erroresDePrecioConfirmados") val erroresDePrecioConfirmados: Int? = null,
    @SerializedName("insignias") val insignias: List<String>? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.TrustProfile {
        return com.smartcart_merchant.features.dashboard.domain.model.TrustProfile(
            trustScore = trustScore ?: 0.0,
            totalRatings = totalCalificaciones ?: 0,
            confirmedPriceErrors = erroresDePrecioConfirmados ?: 0,
            badges = insignias.orEmpty()
        )
    }
}

data class RatingResponseDto(
    @SerializedName("ratingId") val ratingId: String? = null,
    @SerializedName("storeId") val storeId: String? = null,
    @SerializedName("puntuacion") val puntuacion: Int? = null,
    @SerializedName("fechaRegistro") val fechaRegistro: String? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.StoreRating {
        return com.smartcart_merchant.features.dashboard.domain.model.StoreRating(
            ratingId = ratingId.orEmpty(),
            score = puntuacion ?: 0,
            registeredAt = fechaRegistro.orEmpty()
        )
    }
}

data class PriceErrorResponseDto(
    @SerializedName("priceErrorId") val priceErrorId: String? = null,
    @SerializedName("productoId") val productoId: String? = null,
    @SerializedName("discrepancia") val discrepancia: Double? = null,
    @SerializedName("estadoError") val estadoError: String? = null,
    @SerializedName("fechaReporte") val fechaReporte: String? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.PriceErrorItem {
        return com.smartcart_merchant.features.dashboard.domain.model.PriceErrorItem(
            errorId = priceErrorId.orEmpty(),
            productId = productoId.orEmpty(),
            discrepancy = discrepancia ?: 0.0,
            status = estadoError.orEmpty(),
            reportedAt = fechaReporte.orEmpty()
        )
    }
}

data class ConfirmPriceErrorRequestDto(
    @SerializedName("estado") val estado: String
)

data class BulkUploadResponseDto(
    @SerializedName("jobId") val jobId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalItemsProcessed") val totalItemsProcessed: Int? = null,
    @SerializedName("errorsCount") val errorsCount: Int? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("lineErrors") val lineErrors: List<String>? = null
)

data class CreateClearanceRequestDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("discountPercentage") val discountPercentage: Double,
    @SerializedName("expiryDate") val expiryDate: String,
    @SerializedName("reason") val reason: String? = null
)

data class ClearanceResponseDto(
    @SerializedName("storeId") val storeId: Long? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("discountPercentage") val discountPercentage: Double? = null,
    @SerializedName("expiryDate") val expiryDate: String? = null,
    @SerializedName("status") val status: String? = null
)

data class ReviewResponseDto(
    @SerializedName("reviewId") val reviewId: String? = null,
    @SerializedName("comentario") val comentario: String? = null,
    @SerializedName("estadoPublicacion") val estadoPublicacion: String? = null,
    @SerializedName("respuesta") val respuesta: String? = null,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.StoreReview {
        return com.smartcart_merchant.features.dashboard.domain.model.StoreReview(
            reviewId = reviewId.orEmpty(),
            comment = comentario.orEmpty(),
            status = estadoPublicacion.orEmpty(),
            reply = respuesta,
            createdAt = fechaCreacion.orEmpty()
        )
    }
}

data class ReplyReviewRequestDto(
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("respuesta") val respuesta: String
)

data class UpdatePreferencesRequestDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("channels") val channels: List<ChannelResourceDto>,
    @SerializedName("ventanaSilencio") val ventanaSilencio: SilenceWindowDto? = null
)

data class ChannelResourceDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("estaHabilitado") val estaHabilitado: Boolean,
    @SerializedName("tokenContacto") val tokenContacto: String? = null
)

data class SilenceWindowDto(
    @SerializedName("horaInicio") val horaInicio: String?,
    @SerializedName("horaFin") val horaFin: String?
)

data class PreferenceResponseDto(
    @SerializedName("userId") val userId: Long? = null,
    @SerializedName("channels") val channels: List<ChannelItemDto>? = null,
    @SerializedName("silenceWindow") val silenceWindow: SilenceWindowResponseDto? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.NotificationPreferences {
        return com.smartcart_merchant.features.dashboard.domain.model.NotificationPreferences(
            userId = userId ?: 0,
            channels = channels.orEmpty().map {
                com.smartcart_merchant.features.dashboard.domain.model.NotificationChannelPref(
                    type = it.tipo.orEmpty(),
                    enabled = it.estaHabilitado == true,
                    contactToken = it.tokenContacto
                )
            },
            silenceStart = silenceWindow?.horaInicio,
            silenceEnd = silenceWindow?.horaFin
        )
    }
}

data class ChannelItemDto(
    @SerializedName("tipo") val tipo: String? = null,
    @SerializedName("estaHabilitado") val estaHabilitado: Boolean? = null,
    @SerializedName("tokenContacto") val tokenContacto: String? = null
)

data class SilenceWindowResponseDto(
    @SerializedName("horaInicio") val horaInicio: String? = null,
    @SerializedName("horaFin") val horaFin: String? = null
)

data class NotificationHistoryPageDto(
    @SerializedName("content") val content: List<NotificationSummaryDto>? = null,
    @SerializedName("totalElements") val totalElements: Long? = null,
    @SerializedName("totalPages") val totalPages: Int? = null,
    @SerializedName("number") val number: Int? = null,
    @SerializedName("size") val size: Int? = null
)

data class NotificationSummaryDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("canal") val canal: String? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("asunto") val asunto: String? = null,
    @SerializedName("cuerpoResumen") val cuerpoResumen: String? = null,
    @SerializedName("creadoEn") val creadoEn: String? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.NotificationHistoryItem {
        return com.smartcart_merchant.features.dashboard.domain.model.NotificationHistoryItem(
            id = id ?: 0,
            channel = canal.orEmpty(),
            status = estado.orEmpty(),
            subject = asunto.orEmpty(),
            summary = cuerpoResumen.orEmpty(),
            createdAt = creadoEn.orEmpty()
        )
    }
}

data class TestNotificationRequestDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("channel") val channel: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("cuerpo") val cuerpo: String
)

data class NotificationStatusResponseDto(
    @SerializedName("notificationId") val notificationId: Long? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("canal") val canal: String? = null,
    @SerializedName("fechaEnvio") val fechaEnvio: String? = null,
    @SerializedName("intentos") val intentos: Int? = null
)

data class UpdateOperatingHoursRequestDto(
    @SerializedName("operatingHours") val operatingHours: List<OperatingHourDto>
)

data class OperatingHourDto(
    @SerializedName("day") val day: String,
    @SerializedName("open") val open: String,
    @SerializedName("close") val close: String
)

data class CategoryCatalogResponseDto(
    @SerializedName("categories") val categories: List<CategoryEntryDto>? = null
) {
    fun toDomain(): List<com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem> {
        return categories.orEmpty().map {
            com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem(
                id = it.id ?: 0,
                name = it.name.orEmpty(),
                description = it.description.orEmpty()
            )
        }
    }
}

data class CategoryEntryDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null
)

data class CompetitorComparisonResponseDto(
    @SerializedName("storeId") val storeId: Long? = null,
    @SerializedName("radiusMeters") val radiusMeters: Int? = null,
    @SerializedName("storeAveragePrice") val storeAveragePrice: Double? = null,
    @SerializedName("zoneAveragePrice") val zoneAveragePrice: Double? = null,
    @SerializedName("savingsLeader") val savingsLeader: Boolean? = null,
    @SerializedName("leaderBadge") val leaderBadge: String? = null,
    @SerializedName("lastUpdated") val lastUpdated: String? = null,
    @SerializedName("competitors") val competitors: List<CompetitorPriceEntryDto>? = null
) {
    fun toDomain(): com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison {
        return com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison(
            storeId = storeId ?: 0,
            radiusMeters = radiusMeters ?: 500,
            storeAveragePrice = storeAveragePrice ?: 0.0,
            zoneAveragePrice = zoneAveragePrice ?: 0.0,
            savingsLeader = savingsLeader == true,
            leaderBadge = leaderBadge.orEmpty(),
            lastUpdated = lastUpdated.orEmpty(),
            competitors = competitors.orEmpty().map { it.toDomain() }
        )
    }
}

data class CompetitorPriceEntryDto(
    @SerializedName("competitorName") val competitorName: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("productName") val productName: String? = null,
    @SerializedName("competitorPrice") val competitorPrice: Double? = null,
    @SerializedName("storePrice") val storePrice: Double? = null,
    @SerializedName("difference") val difference: Double? = null
) {
    fun toDomain() = com.smartcart_merchant.features.dashboard.domain.model.CompetitorPriceEntry(
        competitorName = competitorName.orEmpty(),
        sku = sku.orEmpty(),
        productName = productName.orEmpty(),
        competitorPrice = competitorPrice ?: 0.0,
        storePrice = storePrice ?: 0.0,
        difference = difference ?: 0.0
    )
}

data class VisibilityRankingResponseDto(
    @SerializedName("storeId") val storeId: Long? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("totalStoresInDistrict") val totalStoresInDistrict: Int? = null,
    @SerializedName("cheapestInDistrict") val cheapestInDistrict: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("evaluatedAt") val evaluatedAt: String? = null
) {
    fun toDomain() = com.smartcart_merchant.features.dashboard.domain.model.VisibilityRanking(
        storeId = storeId ?: 0,
        district = district.orEmpty(),
        rank = rank ?: 0,
        totalStoresInDistrict = totalStoresInDistrict ?: 0,
        cheapestInDistrict = cheapestInDistrict == true,
        message = message.orEmpty(),
        evaluatedAt = evaluatedAt.orEmpty()
    )
}

data class CreateSupportTicketRequestDto(
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

data class SupportTicketResponseDto(
    @SerializedName("ticketId") val ticketId: String? = null,
    @SerializedName("storeId") val storeId: Long? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("resolvedAt") val resolvedAt: String? = null
) {
    fun toDomain() = com.smartcart_merchant.features.dashboard.domain.model.SupportTicket(
        ticketId = ticketId.orEmpty(),
        storeId = storeId ?: 0,
        type = type.orEmpty(),
        description = description.orEmpty(),
        status = status.orEmpty(),
        createdAt = createdAt.orEmpty(),
        resolvedAt = resolvedAt
    )
}

data class ProposeCategoryRequestDto(
    @SerializedName("proposedName") val proposedName: String,
    @SerializedName("description") val description: String
)
