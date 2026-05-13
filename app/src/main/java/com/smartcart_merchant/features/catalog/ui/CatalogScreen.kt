package com.smartcart_merchant.features.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete

@Composable
fun CatalogScreen() {
    val products = List(8) { index ->
        CatalogProductUi(
            name = "Producto ${index + 1}",
            category = "Abarrotes",
            price = "S/ ${6 + index}.00",
            stock = if ((index + 1) % 3 == 0) "Bajo" else "${(index + 1) * 10}",
            lowStock = (index + 1) % 3 == 0
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CatalogHeader()
        CatalogTabs()
        ProductsTable(products)
    }
}

private data class CatalogProductUi(
    val name: String,
    val category: String,
    val price: String,
    val stock: String,
    val lowStock: Boolean
)

@Composable
private fun CatalogHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gestion de Catalogo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {},
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Nuevo producto")
        }
    }
}

@Composable
private fun CatalogTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Carga masiva",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Productos",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2563EB)
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(2.dp)
                    .background(Color(0xFF2563EB))
            )
        }
    }
    HorizontalDivider(color = Color(0xFFE5E7EB))
}

@Composable
private fun ProductsTable(products: List<CatalogProductUi>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TableHeader()
            HorizontalDivider(color = Color(0xFFE5E7EB))
            products.forEach { product ->
                ProductRow(product)
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = false, onCheckedChange = null)
        Spacer(Modifier.width(4.dp))
        Text("Producto", Modifier.weight(2f), fontSize = 12.sp, color = Color(0xFF6B7280))
        Text("Categoria", Modifier.weight(1.4f), fontSize = 12.sp, color = Color(0xFF6B7280))
        Text("Precio", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280))
        Text("Stock", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280))
        Text("Acciones", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280), textAlign = TextAlign.End)
    }
}

@Composable
private fun ProductRow(product: CatalogProductUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = false, onCheckedChange = null)
        Spacer(Modifier.width(4.dp))
        Row(Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5E7EB))
            )
            Spacer(Modifier.width(12.dp))
            Text(product.name, fontSize = 13.sp)
        }
        Text(product.category, Modifier.weight(1.4f), fontSize = 13.sp, color = Color(0xFF6B7280))
        Text(product.price, Modifier.weight(1f), fontSize = 13.sp, color = Color(0xFF2563EB))
        Text(
            product.stock,
            Modifier.weight(1f),
            fontSize = 13.sp,
            color = if (product.lowStock) Color(0xFFEF4444) else Color(0xFF10B981)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Filled.Create, contentDescription = null, tint = Color(0xFFEF4444))
            }
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = Color(0xFF9CA3AF))
            }
        }
    }
}
