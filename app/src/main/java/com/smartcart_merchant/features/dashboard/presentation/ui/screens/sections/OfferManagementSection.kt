package com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.features.dashboard.presentation.state.ActiveOffer
import com.smartcart_merchant.features.dashboard.presentation.state.CatalogProduct
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun OfferManagementSection(
    activeOffers: List<ActiveOffer>,
    catalogProducts: List<CatalogProduct>,
    availableCategories: List<Long>,
    isCreatingOffer: Boolean,
    showOfferDialog: Boolean,
    showBulkOfferDialog: Boolean,
    onCreateOffer: () -> Unit,
    onCreateBulkOffer: () -> Unit,
    onDismissOfferDialog: () -> Unit,
    onDismissBulkOfferDialog: () -> Unit,
    onApplyOffer: (String, Double, String, Boolean) -> Unit,
    onApplyBulkOffer: (List<String>, Double, String, Boolean) -> Unit,
    onApplySeasonalByCategory: (Long, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSeasonalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Crea ofertas individuales o masivas para varios productos a la vez.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardActionButton(
                text = "Nueva oferta",
                onClick = onCreateOffer,
                modifier = Modifier.weight(1f),
                containerColor = SmartBlue,
                enabled = !isCreatingOffer && catalogProducts.isNotEmpty()
            )
            DashboardActionButton(
                text = "Ofertas masivas",
                onClick = onCreateBulkOffer,
                modifier = Modifier.weight(1f),
                containerColor = SmartGreen,
                enabled = !isCreatingOffer && catalogProducts.isNotEmpty()
            )
        }

        if (availableCategories.isNotEmpty()) {
            DashboardActionButton(
                text = "Promoción de temporada (US43)",
                onClick = { showSeasonalDialog = true },
                containerColor = SmartBlue,
                enabled = !isCreatingOffer,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isCreatingOffer) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp))
                Text("Aplicando ofertas...")
            }
        }

        DashboardSectionCard(title = "Ofertas activas (${activeOffers.size})") {
            if (activeOffers.isEmpty()) {
                Text(
                    text = "No hay ofertas activas. Crea una promoción desde el inventario.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                activeOffers.forEach { offer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = offer.productName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                text = "${offer.sku} · ${offer.expiresIn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        Text(
                            text = offer.discount,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SmartGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showOfferDialog) {
        SingleOfferDialog(
            products = catalogProducts,
            onDismiss = onDismissOfferDialog,
            onConfirm = onApplyOffer
        )
    }

    if (showBulkOfferDialog) {
        BulkOfferDialog(
            products = catalogProducts,
            onDismiss = onDismissBulkOfferDialog,
            onConfirm = onApplyBulkOffer
        )
    }

    if (showSeasonalDialog) {
        SeasonalOfferDialog(
            categories = availableCategories,
            onDismiss = { showSeasonalDialog = false },
            onConfirm = { categoryId, discount, expiry ->
                onApplySeasonalByCategory(categoryId, discount, expiry)
                showSeasonalDialog = false
            }
        )
    }
}

@Composable
private fun SingleOfferDialog(
    products: List<CatalogProduct>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Boolean) -> Unit
) {
    var selectedSku by remember { mutableStateOf(products.firstOrNull()?.sku.orEmpty()) }
    var discount by remember { mutableStateOf("10") }
    var expiryDate by remember { mutableStateOf(DashboardViewModel.defaultExpiryDate()) }
    var isFlashSale by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva oferta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = selectedSku,
                    onValueChange = { selectedSku = it },
                    label = { Text("SKU del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text("Descuento (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Fecha fin (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFlashSale, onCheckedChange = { isFlashSale = it })
                    Text("Marcar como oferta relámpago")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val percentage = discount.toDoubleOrNull() ?: return@TextButton
                    onConfirm(selectedSku, percentage, expiryDate, isFlashSale)
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun SeasonalOfferDialog(
    categories: List<Long>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double, String) -> Unit
) {
    var categoryId by remember { mutableStateOf(categories.firstOrNull()?.toString().orEmpty()) }
    var discount by remember { mutableStateOf("20") }
    var expiryDate by remember { mutableStateOf(DashboardViewModel.defaultExpiryDate()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Promoción de temporada") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Aplica descuento a todos los productos de una categoría.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                OutlinedTextField(
                    value = categoryId,
                    onValueChange = { categoryId = it },
                    label = { Text("ID de categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text("Descuento (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Fecha fin (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val cat = categoryId.toLongOrNull() ?: return@TextButton
                val pct = discount.toDoubleOrNull() ?: return@TextButton
                onConfirm(cat, pct, expiryDate)
            }) { Text("Aplicar a categoría") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun BulkOfferDialog(
    products: List<CatalogProduct>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>, Double, String, Boolean) -> Unit
) {
    val selected = remember { mutableStateMapOf<String, Boolean>() }
    var discount by remember { mutableStateOf("15") }
    var expiryDate by remember { mutableStateOf(DashboardViewModel.defaultExpiryDate()) }
    var isFlashSale by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ofertas masivas") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text("Descuento común (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Fecha fin (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                products.forEach { product ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selected[product.sku] == true,
                            onCheckedChange = { checked ->
                                selected[product.sku] = checked
                            }
                        )
                        Text("${product.name} (${product.sku})")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val percentage = discount.toDoubleOrNull() ?: return@TextButton
                    val skus = selected.filterValues { it }.keys.toList()
                    onConfirm(skus, percentage, expiryDate, isFlashSale)
                }
            ) { Text("Aplicar a seleccionados") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
