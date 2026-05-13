package com.smartcart_merchant.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable

// 1. Modelo simple para la tabla (Mock Data)
data class Transaction(
    val id: String,
    val client: String,
    val products: Int,
    val total: String,
    val status: String
)

data class MetricUi(
    val title: String,
    val value: String,
    val trend: String,
    val icon: ImageVector,
    val color: Color
)

data class TopProductUi(
    val rank: Int,
    val name: String,
    val units: String,
    val price: String
)

@Composable
fun DashboardScreen() {
    val metrics = listOf(
        MetricUi("Ventas hoy", "324", "+12%", Icons.Filled.Paid, Color(0xFF3B82F6)),
        MetricUi("Productos", "1,248", "+12%", Icons.Filled.Inventory2, Color(0xFF22C55E)),
        MetricUi("Ofertas activas", "18", "+12%", Icons.Filled.LocalOffer, Color(0xFFF59E0B)),
        MetricUi("Valoración", "4.7", "+12%", Icons.Filled.Star, Color(0xFFF97316))
    )

    val topProducts = listOf(
        TopProductUi(1, "Producto 1", "110 unidades", "S/ 45.00"),
        TopProductUi(2, "Producto 2", "100 unidades", "S/ 40.00"),
        TopProductUi(3, "Producto 3", "90 unidades", "S/ 35.00"),
        TopProductUi(4, "Producto 4", "80 unidades", "S/ 30.00"),
        TopProductUi(5, "Producto 5", "70 unidades", "S/ 25.00")
    )

    val transactions = listOf(
        Transaction("#1001", "Cliente 1", 4, "S/ 30.00", "Completado"),
        Transaction("#1002", "Cliente 2", 5, "S/ 35.00", "Completado"),
        Transaction("#1003", "Cliente 3", 6, "S/ 40.00", "Completado"),
        Transaction("#1004", "Cliente 4", 7, "S/ 45.00", "Completado")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            DashboardHeader()
        }

        // --- FILA 1: Tarjetas de Métricas ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                metrics.forEach { metric ->
                    MetricCard(metric, Modifier.weight(1f))
                }
            }
        }

        // --- FILA 2: Gráfica y Top Productos ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardSection("Ventas por día", Modifier.weight(1.5f)) {
                    SalesChartPlaceholder()
                }
                DashboardSection("Top 10 Productos", Modifier.weight(1f)) {
                    TopProductsList(topProducts)
                }
            }
        }

        // --- FILA 3: Tabla de Transacciones ---
        item {
            DashboardSection("Últimas transacciones", Modifier.fillMaxWidth()) {
                TransactionTable(transactions)
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        FilterChip()
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB))
        )
    }
}

@Composable
private fun FilterChip() {
    Surface(
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Últimos 7 días", fontSize = 12.sp, color = Color(0xFF6B7280))
            Spacer(Modifier.width(6.dp))
            Text("v", fontSize = 12.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
private fun SalesChartPlaceholder() {
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFF9FAFB), Color(0xFFFFFFFF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Gráfica de Ventas", color = Color(0xFF9CA3AF))
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                Text(day, fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
        }
    }
}

@Composable
private fun TopProductsList(items: List<TopProductUi>) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.rank.toString(), fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(item.units, fontSize = 12.sp, color = Color(0xFF9CA3AF))
                }
                Text(item.price, fontSize = 12.sp, color = Color(0xFF3B82F6))
            }
        }
    }
}

@Composable
fun MetricCard(metric: MetricUi, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(metric.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = metric.icon,
                    contentDescription = null,
                    tint = metric.color
                )
            }
            Text(metric.value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = metric.color)
            Text(metric.title, fontSize = 12.sp, color = Color(0xFF6B7280))
            Text(metric.trend, fontSize = 12.sp, color = Color(0xFF10B981))
        }
    }
}

@Composable
fun DashboardSection(title: String, modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color(0xFFE5E7EB))
            content()
        }
    }
}

@Composable
fun TransactionTable(data: List<Transaction>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ID", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280))
            Text("Cliente", Modifier.weight(2f), fontSize = 12.sp, color = Color(0xFF6B7280))
            Text("Productos", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280))
            Text("Total", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280))
            Text("Estado", Modifier.weight(1f), fontSize = 12.sp, color = Color(0xFF6B7280), textAlign = TextAlign.End)
        }

        HorizontalDivider(color = Color(0xFFE5E7EB))

        data.forEach { tx ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tx.id, Modifier.weight(1f), fontSize = 13.sp)
                Text(tx.client, Modifier.weight(2f), fontSize = 13.sp)
                Text(tx.products.toString(), Modifier.weight(1f), fontSize = 13.sp)
                Text(tx.total, Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B82F6))
                StatusPill(tx.status, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD1FAE5))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text, fontSize = 11.sp, color = Color(0xFF10B981))
        }
    }
}
