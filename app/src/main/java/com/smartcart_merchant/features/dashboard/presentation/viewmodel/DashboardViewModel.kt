package com.smartcart_merchant.features.dashboard.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcart_merchant.BuildConfig
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.core.util.CategoryCatalog
import com.smartcart_merchant.core.util.JsonMapHelper
import com.smartcart_merchant.core.util.OperatingHoursJsonHelper
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination
import com.smartcart_merchant.features.dashboard.domain.model.InventoryItem
import com.smartcart_merchant.features.dashboard.domain.model.NewProductInput
import com.smartcart_merchant.features.dashboard.domain.model.TrustProfile
import com.smartcart_merchant.features.dashboard.domain.repository.DashboardRepository
import com.smartcart_merchant.features.dashboard.presentation.state.ActiveOffer
import com.smartcart_merchant.features.dashboard.presentation.state.CatalogProduct
import com.smartcart_merchant.features.dashboard.presentation.state.DashboardMetric
import com.smartcart_merchant.features.dashboard.presentation.state.DashboardUiState
import com.smartcart_merchant.features.dashboard.presentation.state.DemandAlert
import com.smartcart_merchant.features.dashboard.presentation.state.PendingCsvUpload
import com.smartcart_merchant.features.dashboard.presentation.state.StockStatus
import com.smartcart_merchant.features.dashboard.presentation.state.SupportTopic
import com.smartcart_merchant.features.store.domain.model.OperatingHour
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val storeRepository: StoreRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val supportTopics: List<SupportTopic> = listOf(
        SupportTopic(
            question = "¿Cómo subo mi lista de precios?",
            answer = "Ve a Gestión de Catálogo, selecciona un archivo CSV y confirma la carga masiva."
        ),
        SupportTopic(
            question = "¿Qué formato debe tener el archivo?",
            answer = "CSV con columnas: sku,name,brand,categoryId,priceAmount,currency,quantity,minThreshold,promotional,discountPercentage,expiryDate. " +
                "También aceptamos formato simplificado: sku,product_name,price,category."
        ),
        SupportTopic(
            question = "¿Cómo activo ofertas masivas?",
            answer = "En Gestión de Ofertas usa 'Ofertas masivas', selecciona productos y define el descuento común."
        ),
        SupportTopic(
            question = "¿Cómo configuro horarios especiales?",
            answer = "En el Dashboard, edita los horarios de atención. Se guardan en este dispositivo; el backend GitHub main aún no expone actualización de horarios vía API."
        )
    )

    init {
        var lastLoadedStoreId: String? = null
        viewModelScope.launch {
            combine(
                combine(
                    sessionPreferences.companyName,
                    sessionPreferences.ruc,
                    sessionPreferences.merchantId,
                    sessionPreferences.storeId
                ) { companyName, ruc, merchantId, storeId ->
                    listOf(companyName, ruc, merchantId, storeId)
                },
                combine(
                    sessionPreferences.stockSnapshots,
                    sessionPreferences.offerDiscounts,
                    sessionPreferences.storeHoursOverride
                ) { stockSnapshotsJson, offerDiscountsJson, storeHoursJson ->
                    listOf(stockSnapshotsJson, offerDiscountsJson, storeHoursJson)
                }
            ) { sessionValues, prefValues ->
                SessionSnapshot(
                    companyName = sessionValues[0],
                    ruc = sessionValues[1],
                    merchantId = sessionValues[2],
                    storeId = sessionValues[3],
                    stockSnapshotsJson = prefValues[0],
                    offerDiscountsJson = prefValues[1],
                    storeHoursJson = prefValues[2]
                )
            }.collect { snapshot ->
                val stockSnapshots = JsonMapHelper.intMapFromJson(snapshot.stockSnapshotsJson)
                val offerDiscounts = JsonMapHelper.doubleMapFromJson(snapshot.offerDiscountsJson)
                val hoursOverride = OperatingHoursJsonHelper.fromJson(snapshot.storeHoursJson)

                _uiState.update {
                    it.copy(
                        companyName = snapshot.companyName?.takeIf { name -> name.isNotBlank() } ?: "Mi Tienda",
                        ruc = snapshot.ruc.orEmpty(),
                        merchantId = snapshot.merchantId.orEmpty(),
                        storeId = snapshot.storeId.orEmpty(),
                        stockSnapshots = stockSnapshots,
                        offerDiscounts = offerDiscounts,
                        storeHours = hoursOverride.ifEmpty { it.storeHours },
                        qrLandingUrl = snapshot.storeId?.let { id ->
                            "${BuildConfig.LANDING_BASE_URL}?store=$id"
                        }.orEmpty()
                    )
                }

                val storeId = snapshot.storeId.orEmpty()
                if (storeId.isNotBlank() && storeId != lastLoadedStoreId) {
                    lastLoadedStoreId = storeId
                    storeId.toLongOrNull()?.let { loadDashboardData(it) }
                }
            }
        }
    }

    fun onDestinationSelected(destination: DashboardDestination) {
        _uiState.update { it.copy(selectedDestination = destination) }
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            if (destination == DashboardDestination.CATALOG_MANAGEMENT ||
                destination == DashboardDestination.OFFER_MANAGEMENT
            ) {
                refreshInventory(storeId)
            }
            if (destination == DashboardDestination.SUPPORT ||
                destination == DashboardDestination.REVIEWS
            ) {
                loadExperienceData(_uiState.value.storeId, resetReviews = true)
            }
            if (destination == DashboardDestination.NOTIFICATIONS) {
                loadNotificationData(reset = true)
            }
        }
    }

    fun setCategoryFilter(categoryId: Long?) {
        _uiState.update { it.copy(selectedCategoryFilter = categoryId) }
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch { refreshInventory(storeId) }
    }

    fun addProduct(sku: String, name: String, brand: String, categoryId: Long, price: Double, quantity: Int) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (
                val result = dashboardRepository.addProduct(
                    storeId,
                    NewProductInput(sku, name, brand, categoryId, price, "PEN", quantity, 5)
                )
            ) {
                is Resource.Success -> {
                    persistStockSnapshot(sku, quantity)
                    showMessage("Producto $sku agregado")
                    refreshInventory(storeId)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleStock(sku: String) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        val item = _uiState.value.inventoryItems.find { it.sku == sku } ?: return
        val snapshots = _uiState.value.stockSnapshots.toMutableMap()
        val newQty = if (item.stockStatus == StockStatus.OUT_OF_STOCK) {
            snapshots[sku] ?: item.quantity.coerceAtLeast(1).takeIf { it > 0 } ?: 50
        } else {
            snapshots[sku] = item.quantity
            0
        }
        viewModelScope.launch {
            when (val result = dashboardRepository.updateStock(storeId, item, newQty)) {
                is Resource.Success -> {
                    persistStockSnapshot(sku, if (newQty == 0) snapshots[sku] ?: item.quantity else newQty)
                    showMessage(if (newQty == 0) "Producto marcado sin stock" else "Stock repuesto a $newQty unidades")
                    refreshInventory(storeId)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun replyToReview(reviewId: String, reply: String) {
        val storeId = _uiState.value.storeId
        val merchantId = _uiState.value.merchantId
        if (storeId.isBlank() || merchantId.isBlank()) return
        viewModelScope.launch {
            when (val result = dashboardRepository.replyToReview(storeId, reviewId, merchantId, reply)) {
                is Resource.Success -> {
                    showMessage("Respuesta publicada")
                    loadExperienceData(storeId, resetReviews = true)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun loadMoreReviews() {
        val storeId = _uiState.value.storeId
        if (storeId.isBlank() || !_uiState.value.reviewsHasMore || _uiState.value.isLoadingMoreReviews) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreReviews = true) }
            val nextPage = _uiState.value.reviewsPage + 1
            when (val result = dashboardRepository.getAllReviews(storeId, nextPage)) {
                is Resource.Success -> {
                    val page = result.data!!
                    _uiState.update {
                        it.copy(
                            reviews = it.reviews + page.items,
                            reviewsPage = nextPage,
                            reviewsHasMore = page.hasMore,
                            isLoadingMoreReviews = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingMoreReviews = false, error = result.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleNotificationChannel(type: String, enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                notificationPreferences = state.notificationPreferences.copy(
                    channels = state.notificationPreferences.channels.map {
                        if (it.type == type) it.copy(enabled = enabled) else it
                    }
                )
            )
        }
    }

    fun updateSilenceWindow(start: String, end: String) {
        _uiState.update { state ->
            state.copy(
                notificationPreferences = state.notificationPreferences.copy(
                    silenceStart = start.ifBlank { null },
                    silenceEnd = end.ifBlank { null }
                )
            )
        }
    }

    fun saveNotificationPreferences() {
        val prefs = _uiState.value.notificationPreferences.copy(
            userId = _uiState.value.merchantId.toLongOrNull() ?: return
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingNotifications = true) }
            when (val result = dashboardRepository.updateNotificationPreferences(prefs)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            notificationPreferences = result.data ?: prefs,
                            isSavingNotifications = false
                        )
                    }
                    showMessage("Preferencias guardadas")
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSavingNotifications = false, error = result.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun sendTestNotification(channel: String) {
        val userId = _uiState.value.merchantId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingTestNotification = true) }
            when (
                val result = dashboardRepository.sendTestNotification(
                    userId = userId,
                    channel = channel,
                    title = "Prueba SmartCart Merchant",
                    body = "Notificación de prueba desde la app comerciante."
                )
            ) {
                is Resource.Success -> {
                    showMessage("Notificación de prueba enviada (${result.data})")
                    loadNotificationData(reset = true)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
            _uiState.update { it.copy(isSendingTestNotification = false) }
        }
    }

    fun loadMoreNotifications() {
        val userId = _uiState.value.merchantId.toLongOrNull() ?: return
        if (!_uiState.value.notificationsHasMore || _uiState.value.isLoadingMoreNotifications) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreNotifications = true) }
            val nextPage = _uiState.value.notificationsPage + 1
            when (val result = dashboardRepository.getNotificationHistory(userId, nextPage)) {
                is Resource.Success -> {
                    val page = result.data!!
                    _uiState.update {
                        it.copy(
                            notificationHistory = it.notificationHistory + page.items,
                            notificationsPage = nextPage,
                            notificationsHasMore = page.hasMore,
                            isLoadingMoreNotifications = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingMoreNotifications = false, error = result.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun refreshDashboard() {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        loadDashboardData(storeId)
    }

    fun uploadCatalogFile(uri: Uri, fileName: String) {
        val storeId = _uiState.value.storeId.toLongOrNull()
        if (storeId == null) {
            showMessage("Registra una sucursal antes de cargar el catálogo")
            return
        }

        viewModelScope.launch {
            when (val preview = dashboardRepository.previewCsvUpload(uri, fileName)) {
                is Resource.Success -> {
                    val data = preview.data!!
                    if (data.lineErrors.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                lastUploadLineErrors = data.lineErrors,
                                error = "CSV con ${data.lineErrors.size} error(es). Revisa las filas indicadas."
                            )
                        }
                        return@launch
                    }
                    if (data.priceWarnings.isNotEmpty()) {
                        _uiState.update {
                            it.copy(
                                pendingCsvUpload = PendingCsvUpload(
                                    uri = uri,
                                    fileName = fileName,
                                    rowCount = data.rowCount,
                                    lineErrors = data.lineErrors,
                                    priceWarnings = data.priceWarnings
                                )
                            )
                        }
                    } else {
                        executeBulkUpload(storeId, uri, fileName)
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(error = preview.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun confirmPendingCsvUpload() {
        val pending = _uiState.value.pendingCsvUpload ?: return
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        _uiState.update { it.copy(pendingCsvUpload = null) }
        viewModelScope.launch { executeBulkUpload(storeId, pending.uri, pending.fileName) }
    }

    fun dismissPendingCsvUpload() {
        _uiState.update { it.copy(pendingCsvUpload = null) }
    }

    fun showOfferDialog(show: Boolean) {
        _uiState.update { it.copy(showOfferDialog = show) }
    }

    fun showBulkOfferDialog(show: Boolean) {
        _uiState.update { it.copy(showBulkOfferDialog = show) }
    }

    fun showHoursEditor(show: Boolean) {
        _uiState.update { it.copy(showHoursEditor = show) }
    }

    fun saveStoreHours(hours: List<OperatingHour>) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            val json = OperatingHoursJsonHelper.toJson(hours)
            sessionPreferences.saveStoreHoursOverride(json)
            when (val result = dashboardRepository.updateOperatingHours(storeId, hours)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            storeHours = hours,
                            showHoursEditor = false,
                            storeOpenStatus = computeOpenStatus(hours),
                            is24Hours = is24HourSchedule(hours)
                        )
                    }
                    showMessage("Horarios especiales guardados en este dispositivo")
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun setCompetitorRadius(radiusM: Int) {
        _uiState.update { it.copy(competitorRadiusM = radiusM) }
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        loadMerchantInsights(storeId)
    }

    fun toggleVisibilityAlerts(enabled: Boolean) {
        _uiState.update { it.copy(visibilityAlertsEnabled = enabled) }
    }

    fun createSupportTicket(type: String, description: String, lat: Double?, lng: Double?) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (val result = dashboardRepository.createSupportTicket(storeId, type, description, lat, lng)) {
                is Resource.Success -> {
                    showMessage("Ticket ${result.data?.ticketId} creado")
                    loadSupportTickets(storeId)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun submitTicketFeedback(ticketId: String, score: Int) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (val result = dashboardRepository.submitTicketFeedback(storeId, ticketId, score)) {
                is Resource.Success -> {
                    showMessage("Gracias por tu calificación")
                    loadSupportTickets(storeId)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun proposeCategory(name: String, description: String) {
        val storeId = _uiState.value.storeId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (val result = dashboardRepository.proposeCategory(storeId, name, description)) {
                is Resource.Success -> showMessage(result.data ?: "Categoría propuesta")
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun createOffer(
        sku: String,
        discountPercentage: Double,
        expiryDate: String,
        isFlashSale: Boolean
    ) {
        applyOffers(listOf(sku), discountPercentage, expiryDate, isFlashSale)
    }

    fun createBulkOffers(
        skus: List<String>,
        discountPercentage: Double,
        expiryDate: String,
        isFlashSale: Boolean
    ) {
        applyOffers(skus, discountPercentage, expiryDate, isFlashSale)
    }

    fun createSeasonalOffersByCategory(
        categoryId: Long,
        discountPercentage: Double,
        expiryDate: String
    ) {
        val skus = _uiState.value.inventoryItems
            .filter { it.categoryId == categoryId && !it.promotional }
            .map { it.sku }
        applyOffers(skus, discountPercentage, expiryDate, isFlashSale = false, reason = "Promoción de temporada")
    }

    fun confirmPriceError(errorId: String, confirmed: Boolean) {
        val storeId = _uiState.value.storeId
        if (storeId.isBlank()) return

        viewModelScope.launch {
            when (val result = dashboardRepository.confirmPriceError(storeId, errorId, confirmed)) {
                is Resource.Success -> {
                    showMessage(if (confirmed) "Error de precio confirmado" else "Error de precio rechazado")
                    loadExperienceData(storeId, resetReviews = false)
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun applyOffers(
        skus: List<String>,
        discountPercentage: Double,
        expiryDate: String,
        isFlashSale: Boolean,
        reason: String? = null
    ) {
        val storeId = _uiState.value.storeId.toLongOrNull()
        if (storeId == null) {
            showMessage("Registra una sucursal antes de crear ofertas")
            return
        }
        if (skus.isEmpty()) {
            showMessage("Selecciona al menos un producto")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCreatingOffer = true,
                    showOfferDialog = false,
                    showBulkOfferDialog = false
                )
            }

            var successCount = 0
            var lastError: String? = null
            val offerReason = reason ?: if (isFlashSale) "Oferta relámpago" else "Promoción de temporada"
            val inventoryItems = _uiState.value.inventoryItems
            val discounts = _uiState.value.offerDiscounts.toMutableMap()

            skus.forEach { sku ->
                val item = inventoryItems.find { it.sku.equals(sku, ignoreCase = true) }
                if (item == null) {
                    lastError = "Producto $sku no encontrado en inventario"
                    return@forEach
                }
                when (
                    val result = dashboardRepository.applyClearance(
                        storeId = storeId,
                        item = item,
                        discountPercentage = discountPercentage,
                        expiryDate = expiryDate,
                        reason = offerReason
                    )
                ) {
                    is Resource.Success -> {
                        successCount++
                        discounts[item.sku] = discountPercentage
                    }
                    is Resource.Error -> lastError = result.message
                    is Resource.Loading -> Unit
                }
            }

            persistOfferDiscounts(discounts)
            refreshInventory(storeId)
            _uiState.update { it.copy(isCreatingOffer = false) }

            if (successCount > 0) {
                showMessage("Se aplicaron $successCount oferta(s) correctamente")
            } else {
                _uiState.update { it.copy(error = lastError ?: "No se pudo crear la oferta") }
            }
        }
    }

    private suspend fun executeBulkUpload(storeId: Long, uri: Uri, fileName: String) {
        _uiState.update { it.copy(isUploading = true, error = null) }
        when (val result = dashboardRepository.uploadBulkInventory(storeId, uri, fileName)) {
            is Resource.Success -> {
                val upload = result.data!!
                val errorDetail = if (upload.lineErrors.isNotEmpty()) {
                    " · ${upload.lineErrors.size} fila(s) omitida(s)"
                } else {
                    ""
                }
                showMessage(
                    "Catálogo: ${upload.totalItemsProcessed} OK, ${upload.errorsCount} fallidos"
                )
                _uiState.update {
                    it.copy(
                        lastUploadLineErrors = upload.lineErrors,
                        lastUploadSummary = "Procesados ${upload.totalItemsProcessed}, errores ${upload.errorsCount}"
                    )
                }
                refreshInventory(storeId)
            }
            is Resource.Error -> {
                _uiState.update { it.copy(isUploading = false, error = result.message) }
            }
            is Resource.Loading -> Unit
        }
        _uiState.update { it.copy(isUploading = false) }
    }

    private fun loadDashboardData(storeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val storeResult = storeRepository.getStore(storeId)) {
                is Resource.Success -> {
                    val store = storeResult.data
                    val override = sessionPreferences.storeHoursOverride.first()
                    val hours = OperatingHoursJsonHelper.fromJson(override)
                        .ifEmpty { store?.operatingHours.orEmpty() }
                    _uiState.update {
                        it.copy(
                            companyName = store?.name?.takeIf { name -> name.isNotBlank() } ?: it.companyName,
                            storeHours = hours,
                            storeOpenStatus = computeOpenStatus(hours),
                            is24Hours = is24HourSchedule(hours)
                        )
                    }
                }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }

            loadCategories()
            loadMerchantInsights(storeId)

            when (val analyticsResult = dashboardRepository.getAnalytics(storeId)) {
                is Resource.Success -> applyAnalytics(analyticsResult.data!!)
                is Resource.Error -> _uiState.update { it.copy(error = analyticsResult.message) }
                is Resource.Loading -> Unit
            }

            refreshInventory(storeId, showLoading = false)
            loadExperienceData(storeId.toString(), resetReviews = true)
            loadNotificationData(reset = true)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadExperienceData(storeId: String, resetReviews: Boolean) {
        if (storeId.isBlank()) return

        viewModelScope.launch {
            var trustProfile = _uiState.value.trustProfile
            var averageRating = _uiState.value.averageRating
            var ratings = _uiState.value.ratings
            var priceErrors = _uiState.value.priceErrors
            var reviews = if (resetReviews) emptyList() else _uiState.value.reviews

            when (val result = dashboardRepository.getTrustProfile(storeId)) {
                is Resource.Success -> trustProfile = result.data ?: TrustProfile()
                is Resource.Error -> showMessage(result.message ?: "Error al cargar reputación")
                is Resource.Loading -> Unit
            }

            when (val result = dashboardRepository.getRatings(storeId)) {
                is Resource.Success -> {
                    ratings = result.data.orEmpty()
                    averageRating = if (ratings.isEmpty()) 0.0 else ratings.map { it.score }.average()
                }
                is Resource.Error -> showMessage(result.message ?: "Error al cargar calificaciones")
                is Resource.Loading -> Unit
            }

            val reviewPage = if (resetReviews) 0 else _uiState.value.reviewsPage
            when (val result = dashboardRepository.getAllReviews(storeId, reviewPage)) {
                is Resource.Success -> {
                    val page = result.data!!
                    reviews = if (resetReviews) page.items else reviews + page.items
                    _uiState.update {
                        it.copy(
                            reviewsPage = reviewPage,
                            reviewsHasMore = page.hasMore
                        )
                    }
                }
                is Resource.Error -> showMessage(result.message ?: "Error al cargar reseñas")
                is Resource.Loading -> Unit
            }

            when (val result = dashboardRepository.getPriceErrors(storeId)) {
                is Resource.Success -> priceErrors = result.data.orEmpty()
                is Resource.Error -> showMessage(result.message ?: "Error al cargar errores de precio")
                is Resource.Loading -> Unit
            }

            _uiState.update {
                it.copy(
                    trustProfile = trustProfile,
                    averageRating = averageRating,
                    ratings = ratings,
                    reviews = reviews,
                    priceErrors = priceErrors
                )
            }
        }
    }

    private fun loadMerchantInsights(storeId: Long) {
        viewModelScope.launch {
            val radius = _uiState.value.competitorRadiusM
            when (val result = dashboardRepository.getCompetitorComparison(storeId, radius)) {
                is Resource.Success -> _uiState.update { it.copy(competitorComparison = result.data) }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
            when (val result = dashboardRepository.getVisibilityRanking(storeId)) {
                is Resource.Success -> {
                    val ranking = result.data
                    _uiState.update { it.copy(visibilityRanking = ranking) }
                    if (_uiState.value.visibilityAlertsEnabled && ranking != null) {
                        showMessage(ranking.message)
                    }
                }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = dashboardRepository.getCategories()) {
                is Resource.Success -> _uiState.update { it.copy(categories = result.data.orEmpty()) }
                is Resource.Error -> _uiState.update { it.copy(categories = CategoryCatalog.defaults.map {
                    com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem(it.id, it.name, it.description)
                }) }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadSupportTickets(storeId: Long) {
        viewModelScope.launch {
            when (val result = dashboardRepository.listSupportTickets(storeId)) {
                is Resource.Success -> _uiState.update { it.copy(supportTickets = result.data.orEmpty()) }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadNotificationData(reset: Boolean) {
        val userId = _uiState.value.merchantId.toLongOrNull() ?: return
        viewModelScope.launch {
            var prefs = _uiState.value.notificationPreferences
            var history = if (reset) emptyList() else _uiState.value.notificationHistory
            val page = if (reset) 0 else _uiState.value.notificationsPage

            when (val result = dashboardRepository.getNotificationPreferences(userId)) {
                is Resource.Success -> prefs = result.data ?: prefs
                is Resource.Error -> showMessage(result.message ?: "Error al cargar preferencias")
                is Resource.Loading -> Unit
            }
            when (val result = dashboardRepository.getNotificationHistory(userId, page)) {
                is Resource.Success -> {
                    val paged = result.data!!
                    history = if (reset) paged.items else history + paged.items
                    _uiState.update {
                        it.copy(
                            notificationsPage = page,
                            notificationsHasMore = paged.hasMore
                        )
                    }
                }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
            _uiState.update { it.copy(notificationPreferences = prefs, notificationHistory = history) }
        }
    }

    private suspend fun refreshInventory(storeId: Long, showLoading: Boolean = true) {
        if (showLoading) {
            _uiState.update { it.copy(isLoading = true) }
        }

        val categoryId = _uiState.value.selectedCategoryFilter
        when (val inventoryResult = dashboardRepository.getInventory(storeId, categoryId)) {
            is Resource.Success -> applyInventory(inventoryResult.data!!)
            is Resource.Error -> _uiState.update { it.copy(error = inventoryResult.message) }
            is Resource.Loading -> Unit
        }

        if (showLoading) {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun applyAnalytics(analytics: com.smartcart_merchant.features.dashboard.domain.model.StoreAnalytics) {
        val conversion = if (analytics.conversionRate <= 1.0) {
            analytics.conversionRate * 100
        } else {
            analytics.conversionRate
        }

        if (analytics.totalViews == 0L &&
            analytics.abandonedCarts == 0 &&
            analytics.conversionRate == 0.0 &&
            analytics.topProducts.isEmpty()
        ) {
            _uiState.update {
                it.copy(
                    metrics = emptyList(),
                    topDemandProducts = listOf("Sin datos de demanda todavía"),
                    demandAlerts = emptyList(),
                    qrVisitCount = 0
                )
            }
            return
        }

        val topLabels = resolveTopProductLabels(analytics.topProducts, _uiState.value.inventoryItems)

        val metrics = listOf(
            DashboardMetric(
                label = "Visitas landing QR",
                value = analytics.totalViews.toString(),
                trend = "Visitas registradas vía analytics del backend"
            ),
            DashboardMetric(
                label = "Carritos abandonados",
                value = analytics.abandonedCarts.toString(),
                trend = if (analytics.abandonedCarts > 0) "Revisa precios y stock" else "Sin abandonos recientes"
            ),
            DashboardMetric(
                label = "Productos en demanda",
                value = analytics.topProducts.size.toString(),
                trend = "Según actividad de clientes en tu zona"
            ),
            DashboardMetric(
                label = "Conversión",
                value = "${"%.1f".format(conversion)}%",
                trend = if (conversion > 0) "Meta recomendada: 15%" else "Sin datos de conversión"
            )
        )

        val demandAlerts = buildDemandAlerts(topLabels, _uiState.value.inventoryItems)
        val suggestion = if (topLabels.size >= 3) {
            "Sugerencia: aplica oferta del 10-15% en ${topLabels.last()} para rotar inventario (US25 E3)."
        } else {
            null
        }

        _uiState.update {
            it.copy(
                metrics = metrics,
                topDemandProducts = topLabels.ifEmpty { listOf("Sin datos de demanda todavía") },
                demandAlerts = demandAlerts,
                qrVisitCount = analytics.totalViews,
                weeklyOfferSuggestion = suggestion
            )
        }
    }

    private fun resolveTopProductLabels(
        skusOrNames: List<String>,
        inventory: List<InventoryItem>
    ): List<String> {
        return skusOrNames.take(10).map { label ->
            inventory.find { it.sku.equals(label, ignoreCase = true) }?.name ?: label
        }
    }

    private fun applyInventory(items: List<InventoryItem>) {
        val snapshots = _uiState.value.stockSnapshots.toMutableMap()
        items.forEach { item ->
            if (item.stockStatus != StockStatus.OUT_OF_STOCK && !snapshots.containsKey(item.sku)) {
                snapshots[item.sku] = item.quantity
            }
        }

        val categoryMap = (_uiState.value.categories.ifEmpty {
            CategoryCatalog.defaults.map {
                com.smartcart_merchant.features.dashboard.domain.model.RetailCategoryItem(it.id, it.name, it.description)
            }
        }).associate { it.id to it.name }

        val catalog = items.map { item ->
            CatalogProduct(
                sku = item.sku,
                name = item.name,
                price = item.price,
                categoryId = item.categoryId,
                categoryName = categoryMap[item.categoryId] ?: CategoryCatalog.nameFor(item.categoryId),
                stockStatus = item.stockStatus,
                promotional = item.promotional
            )
        }

        val discounts = _uiState.value.offerDiscounts
        val offers = items.filter { it.promotional }.map { item ->
            val pct = discounts[item.sku]
            ActiveOffer(
                sku = item.sku,
                productName = item.name,
                discount = pct?.let { "-${"%.0f".format(it)}%" } ?: "Promo activa",
                expiresIn = item.expiryDate?.takeIf { it.isNotBlank() } ?: "Vigente",
                isFlashSale = pct != null && pct >= 20,
                categoryId = item.categoryId
            )
        }

        val demandAlerts = buildDemandAlerts(_uiState.value.topDemandProducts, items)

        _uiState.update {
            it.copy(
                catalogProducts = catalog,
                inventoryItems = items,
                activeOffers = offers,
                stockSnapshots = snapshots,
                demandAlerts = demandAlerts
            )
        }
    }

    private fun buildDemandAlerts(
        topProducts: List<String>,
        inventory: List<InventoryItem>
    ): List<DemandAlert> {
        if (topProducts.isEmpty() || inventory.isEmpty()) return emptyList()
        return topProducts.mapNotNull { label ->
            val item = inventory.find {
                it.name.equals(label, ignoreCase = true) ||
                    it.sku.equals(label, ignoreCase = true)
            } ?: return@mapNotNull null
            if (item.stockStatus == StockStatus.LOW_STOCK || item.stockStatus == StockStatus.OUT_OF_STOCK) {
                DemandAlert(
                    productName = item.name,
                    sku = item.sku,
                    message = "Alta demanda detectada — stock ${item.stockStatus.label.lowercase()}"
                )
            } else {
                null
            }
        }
    }

    private fun computeOpenStatus(hours: List<OperatingHour>): String {
        if (hours.isEmpty()) return "Horario no configurado"
        if (is24HourSchedule(hours)) return "Abierto 24 horas"
        val now = LocalTime.now()
        val today = DayOfWeek.from(java.time.LocalDate.now()).name
        val todayHour = hours.find { it.day.equals(today, ignoreCase = true) } ?: return "Cerrado hoy"
        return try {
            val open = LocalTime.parse(todayHour.open.take(5))
            val close = LocalTime.parse(todayHour.close.take(5))
            if (!now.isBefore(open) && now.isBefore(close)) "Abierto ahora" else "Cerrado ahora"
        } catch (_: Exception) {
            "Horario configurado"
        }
    }

    private fun is24HourSchedule(hours: List<OperatingHour>): Boolean {
        return hours.isNotEmpty() && hours.all {
            it.open.startsWith("00:00") && (it.close.startsWith("23:59") || it.close.startsWith("24:00"))
        }
    }

    private fun persistStockSnapshot(sku: String, quantity: Int) {
        viewModelScope.launch {
            val updated = _uiState.value.stockSnapshots.toMutableMap().apply { put(sku, quantity) }
            sessionPreferences.saveStockSnapshots(JsonMapHelper.intMapToJson(updated))
            _uiState.update { it.copy(stockSnapshots = updated) }
        }
    }

    private suspend fun persistOfferDiscounts(discounts: Map<String, Double>) {
        sessionPreferences.saveOfferDiscounts(JsonMapHelper.doubleMapToJson(discounts))
        _uiState.update { it.copy(offerDiscounts = discounts) }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    private data class SessionSnapshot(
        val companyName: String?,
        val ruc: String?,
        val merchantId: String?,
        val storeId: String?,
        val stockSnapshotsJson: String?,
        val offerDiscountsJson: String?,
        val storeHoursJson: String?
    )

    companion object {
        fun defaultExpiryDate(): String = LocalDate.now().plusDays(7).toString()
    }
}
