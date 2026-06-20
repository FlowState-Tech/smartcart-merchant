package com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.features.dashboard.presentation.state.CatalogProduct
import com.smartcart_merchant.features.dashboard.presentation.state.StockStatus
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.features.dashboard.presentation.ui.components.StockStatusBadge
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartCyan
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun CatalogManagementSection(
    products: List<CatalogProduct>,
    isUploading: Boolean,
    lastUploadLineErrors: List<String>,
    availableCategories: List<Long>,
    selectedCategoryFilter: Long?,
    onCategoryFilterChanged: (Long?) -> Unit,
    onSelectCsv: () -> Unit,
    onAddProduct: (String, String, String, Long, Double, Int) -> Unit,
    onToggleStock: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sube un archivo CSV o agrega productos individualmente.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        if (availableCategories.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { onCategoryFilterChanged(null) },
                    label = { Text("Todas") }
                )
                availableCategories.forEach { categoryId ->
                    FilterChip(
                        selected = selectedCategoryFilter == categoryId,
                        onClick = { onCategoryFilterChanged(categoryId) },
                        label = { Text("Cat. $categoryId") }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardActionButton(
                text = "Agregar producto",
                onClick = { showAddDialog = true },
                containerColor = SmartGreen,
                modifier = Modifier.weight(1f)
            )
            DashboardActionButton(
                text = if (isUploading) "Procesando..." else "Subir CSV",
                onClick = onSelectCsv,
                containerColor = SmartBlue,
                enabled = !isUploading,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SmartCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .background(SmartCyan.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.UploadFile, null, tint = SmartBlue, modifier = Modifier.size(32.dp))
            Text(
                text = "CSV: sku,name,brand,categoryId,priceAmount,currency,quantity,minThreshold[,promotional,discount,expiry]",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
            if (isUploading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }

        if (lastUploadLineErrors.isNotEmpty()) {
            DashboardSectionCard(title = "Errores en última carga (US11)") {
                lastUploadLineErrors.forEach { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        DashboardSectionCard(title = "Inventario (${products.size} productos)") {
            if (products.isEmpty()) {
                Text(
                    text = "Aún no hay productos. Sube tu catálogo o agrega uno manualmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                products.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                text = "${product.sku} · ${product.price} · Cat. ${product.categoryId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StockStatusBadge(status = product.stockStatus)
                            TextButton(onClick = { onToggleStock(product.sku) }) {
                                Text(
                                    text = if (product.stockStatus == StockStatus.OUT_OF_STOCK) "Reponer" else "Sin stock",
                                    color = SmartBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { sku, name, brand, category, price, qty ->
                onAddProduct(sku, name, brand, category, price, qty)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, Double, Int) -> Unit
) {
    var sku by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Generic") }
    var category by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(sku, { sku = it }, label = { Text("SKU") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(brand, { brand = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(category, { category = it }, label = { Text("Categoría ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(price, { price = it }, label = { Text("Precio (PEN)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(quantity, { quantity = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val priceVal = price.toDoubleOrNull() ?: return@TextButton
                val qtyVal = quantity.toIntOrNull() ?: return@TextButton
                val catVal = category.toLongOrNull() ?: 1L
                if (sku.isNotBlank() && name.isNotBlank()) {
                    onConfirm(sku, name, brand, catVal, priceVal, qtyVal)
                }
            }) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
