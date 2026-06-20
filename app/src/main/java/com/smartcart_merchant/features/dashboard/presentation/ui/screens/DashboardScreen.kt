package com.smartcart_merchant.features.dashboard.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcart_merchant.core.util.QrCodeGenerator
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardNavigationRail
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.CatalogManagementSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.DashboardHomeSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.NotificationsSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.OfferManagementSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.ReviewsSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.StoreQrSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.SupportSection
import com.smartcart_merchant.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.smartcart_merchant.features.store.domain.model.OperatingHour
import java.io.File
import java.io.FileOutputStream

private val ContentBackground = Color(0xFFF5F5F5)

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val supportTopics = viewModel.supportTopics
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val csvPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else "catalog.csv"
            } ?: "catalog.csv"
            viewModel.uploadCatalogFile(uri, fileName)
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    uiState.pendingCsvUpload?.let { pending ->
        AlertDialog(
            onDismissRequest = viewModel::dismissPendingCsvUpload,
            title = { Text("Confirmar precios extremos (US16)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${pending.rowCount} filas listas para subir.")
                    Text("Se detectaron ${pending.priceWarnings.size} precio(s) muy alto(s):")
                    pending.priceWarnings.take(5).forEach { warning ->
                        Text(warning, style = MaterialTheme.typography.bodySmall)
                    }
                    if (pending.priceWarnings.size > 5) {
                        Text("… y ${pending.priceWarnings.size - 5} más", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmPendingCsvUpload) { Text("Confirmar y subir") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissPendingCsvUpload) { Text("Cancelar") }
            }
        )
    }

    if (uiState.showHoursEditor) {
        StoreHoursEditorDialog(
            initialHours = uiState.storeHours,
            onDismiss = { viewModel.showHoursEditor(false) },
            onSave = viewModel::saveStoreHours
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DashboardNavigationRail(
                selectedDestination = uiState.selectedDestination,
                onDestinationSelected = viewModel::onDestinationSelected,
                onLogout = onLogout,
                modifier = Modifier.fillMaxHeight()
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                val availableCategories = uiState.inventoryItems
                    .map { it.categoryId }
                    .distinct()
                    .sorted()

                DashboardContent(
                    selectedDestination = uiState.selectedDestination,
                    uiState = uiState,
                    supportTopics = supportTopics,
                    availableCategories = availableCategories,
                    onSelectCsv = { csvPicker.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv")) },
                    onRefresh = viewModel::refreshDashboard,
                    onEditHours = { viewModel.showHoursEditor(true) },
                    onCompetitorRadiusChanged = viewModel::setCompetitorRadius,
                    onCreateOffer = viewModel::showOfferDialog,
                    onCreateBulkOffer = viewModel::showBulkOfferDialog,
                    onApplyOffer = viewModel::createOffer,
                    onApplyBulkOffer = viewModel::createBulkOffers,
                    onApplySeasonalByCategory = viewModel::createSeasonalOffersByCategory,
                    onConfirmPriceError = viewModel::confirmPriceError,
                    onAddProduct = viewModel::addProduct,
                    onToggleStock = viewModel::toggleStock,
                    onCategoryFilterChanged = viewModel::setCategoryFilter,
                    onReplyReview = viewModel::replyToReview,
                    onLoadMoreReviews = viewModel::loadMoreReviews,
                    onToggleNotificationChannel = viewModel::toggleNotificationChannel,
                    onSilenceWindowChanged = viewModel::updateSilenceWindow,
                    onSaveNotificationPreferences = viewModel::saveNotificationPreferences,
                    onSendTestNotification = viewModel::sendTestNotification,
                    onLoadMoreNotifications = viewModel::loadMoreNotifications,
                    visibilityAlertsEnabled = uiState.visibilityAlertsEnabled,
                    onToggleVisibilityAlerts = viewModel::toggleVisibilityAlerts,
                    onDismissOfferDialog = { viewModel.showOfferDialog(false) },
                    onDismissBulkOfferDialog = { viewModel.showBulkOfferDialog(false) },
                    onDownloadQr = {
                        if (uiState.qrLandingUrl.isBlank()) return@DashboardContent
                        val bitmap = QrCodeGenerator.generateBitmap(uiState.qrLandingUrl)
                        val cacheDir = File(context.cacheDir, "qr")
                        cacheDir.mkdirs()
                        val file = File(cacheDir, "smartcart-store-qr.png")
                        FileOutputStream(file).use { out ->
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                        }
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir QR"))
                    },
                    onPrintQr = {
                        if (uiState.qrLandingUrl.isBlank()) return@DashboardContent
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.qrLandingUrl))
                        context.startActivity(intent)
                    },
                    onReportLocation = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:soporte@flowstatetech.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Corrección de ubicación - ${uiState.companyName}")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Hola soporte,\n\nNecesito corregir la ubicación de mi tienda.\n" +
                                    "Store ID: ${uiState.storeId}\n" +
                                    "RUC: ${uiState.ruc}\n" +
                                    "Comerciante: ${uiState.companyName}\n"
                            )
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "Reportar ubicación"))
                    },
                    onContactSupport = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:soporte@flowstatetech.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Soporte SmartCart Merchant")
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "Contactar soporte"))
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (uiState.isLoading && uiState.metrics.isEmpty() && uiState.catalogProducts.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun StoreHoursEditorDialog(
    initialHours: List<OperatingHour>,
    onDismiss: () -> Unit,
    onSave: (List<OperatingHour>) -> Unit
) {
    val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    val hours = remember {
        mutableStateListOf<OperatingHour>().apply {
            if (initialHours.isEmpty()) {
                days.forEach { day -> add(OperatingHour(day, "08:00", "22:00")) }
            } else {
                addAll(initialHours)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Horarios especiales") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hours.forEachIndexed { index, hour ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = hour.open,
                            onValueChange = { hours[index] = hour.copy(open = it) },
                            label = { Text(hour.day.take(3)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = hour.close,
                            onValueChange = { hours[index] = hour.copy(close = it) },
                            label = { Text("Cierra") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(hours.toList()) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DashboardContent(
    selectedDestination: DashboardDestination,
    uiState: com.smartcart_merchant.features.dashboard.presentation.state.DashboardUiState,
    supportTopics: List<com.smartcart_merchant.features.dashboard.presentation.state.SupportTopic>,
    availableCategories: List<Long>,
    onSelectCsv: () -> Unit,
    onRefresh: () -> Unit,
    onEditHours: () -> Unit,
    onCompetitorRadiusChanged: (Int) -> Unit,
    onCreateOffer: (Boolean) -> Unit,
    onCreateBulkOffer: (Boolean) -> Unit,
    onApplyOffer: (String, Double, String, Boolean) -> Unit,
    onApplyBulkOffer: (List<String>, Double, String, Boolean) -> Unit,
    onApplySeasonalByCategory: (Long, Double, String) -> Unit,
    onConfirmPriceError: (String, Boolean) -> Unit,
    onAddProduct: (String, String, String, Long, Double, Int) -> Unit,
    onToggleStock: (String) -> Unit,
    onCategoryFilterChanged: (Long?) -> Unit,
    onReplyReview: (String, String) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onToggleNotificationChannel: (String, Boolean) -> Unit,
    onSilenceWindowChanged: (String, String) -> Unit,
    onSaveNotificationPreferences: () -> Unit,
    onSendTestNotification: (String) -> Unit,
    onLoadMoreNotifications: () -> Unit,
    visibilityAlertsEnabled: Boolean,
    onToggleVisibilityAlerts: (Boolean) -> Unit,
    onDismissOfferDialog: () -> Unit,
    onDismissBulkOfferDialog: () -> Unit,
    onDownloadQr: () -> Unit,
    onPrintQr: () -> Unit,
    onReportLocation: () -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ContentBackground)
            .padding(24.dp)
    ) {
        Text(
            text = selectedDestination.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )

        when (selectedDestination) {
            DashboardDestination.DASHBOARD -> DashboardHomeSection(
                companyName = uiState.companyName,
                ruc = uiState.ruc,
                metrics = uiState.metrics,
                topDemandProducts = uiState.topDemandProducts,
                demandAlerts = uiState.demandAlerts,
                competitorComparison = uiState.competitorComparison,
                visibilityRanking = uiState.visibilityRanking,
                competitorRadiusM = uiState.competitorRadiusM,
                storeHours = uiState.storeHours,
                storeOpenStatus = uiState.storeOpenStatus,
                is24Hours = uiState.is24Hours,
                weeklyOfferSuggestion = uiState.weeklyOfferSuggestion,
                trustProfile = uiState.trustProfile,
                averageRating = uiState.averageRating,
                ratings = uiState.ratings,
                onRefresh = onRefresh,
                onEditHours = onEditHours,
                onCompetitorRadiusChanged = onCompetitorRadiusChanged,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.CATALOG_MANAGEMENT -> CatalogManagementSection(
                products = uiState.catalogProducts,
                isUploading = uiState.isUploading,
                lastUploadLineErrors = uiState.lastUploadLineErrors,
                availableCategories = availableCategories,
                selectedCategoryFilter = uiState.selectedCategoryFilter,
                onCategoryFilterChanged = onCategoryFilterChanged,
                onSelectCsv = onSelectCsv,
                onAddProduct = onAddProduct,
                onToggleStock = onToggleStock,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.OFFER_MANAGEMENT -> OfferManagementSection(
                activeOffers = uiState.activeOffers,
                catalogProducts = uiState.catalogProducts,
                availableCategories = availableCategories,
                isCreatingOffer = uiState.isCreatingOffer,
                showOfferDialog = uiState.showOfferDialog,
                showBulkOfferDialog = uiState.showBulkOfferDialog,
                onCreateOffer = { onCreateOffer(true) },
                onCreateBulkOffer = { onCreateBulkOffer(true) },
                onDismissOfferDialog = onDismissOfferDialog,
                onDismissBulkOfferDialog = onDismissBulkOfferDialog,
                onApplyOffer = onApplyOffer,
                onApplyBulkOffer = onApplyBulkOffer,
                onApplySeasonalByCategory = onApplySeasonalByCategory,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.STORE_QR -> StoreQrSection(
                companyName = uiState.companyName,
                storeId = uiState.storeId,
                qrLandingUrl = uiState.qrLandingUrl,
                scanCount = uiState.qrVisitCount.toString(),
                onDownloadQr = onDownloadQr,
                onPrintQr = onPrintQr,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.REVIEWS -> ReviewsSection(
                reviews = uiState.reviews,
                merchantId = uiState.merchantId,
                hasMore = uiState.reviewsHasMore,
                isLoadingMore = uiState.isLoadingMoreReviews,
                onReply = onReplyReview,
                onLoadMore = onLoadMoreReviews,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.NOTIFICATIONS -> NotificationsSection(
                preferences = uiState.notificationPreferences,
                history = uiState.notificationHistory,
                isSaving = uiState.isSavingNotifications,
                isSendingTest = uiState.isSendingTestNotification,
                hasMoreHistory = uiState.notificationsHasMore,
                isLoadingMore = uiState.isLoadingMoreNotifications,
                onToggleChannel = onToggleNotificationChannel,
                onSilenceWindowChanged = onSilenceWindowChanged,
                onSavePreferences = onSaveNotificationPreferences,
                onSendTest = onSendTestNotification,
                onLoadMore = onLoadMoreNotifications,
                visibilityAlertsEnabled = visibilityAlertsEnabled,
                onToggleVisibilityAlerts = onToggleVisibilityAlerts,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.SUPPORT -> SupportSection(
                topics = supportTopics,
                priceErrors = uiState.priceErrors,
                onReportLocation = onReportLocation,
                onContactSupport = onContactSupport,
                onConfirmPriceError = onConfirmPriceError,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
        }
    }
}
