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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.features.dashboard.domain.model.StoreRating
import com.smartcart_merchant.features.dashboard.domain.model.TrustProfile
import com.smartcart_merchant.features.dashboard.presentation.state.DashboardMetric
import com.smartcart_merchant.features.dashboard.presentation.state.DemandAlert
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardMetricCard
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.features.dashboard.presentation.ui.components.accentColors
import com.smartcart_merchant.features.store.domain.model.OperatingHour
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun DashboardHomeSection(
    companyName: String,
    ruc: String,
    metrics: List<DashboardMetric>,
    topDemandProducts: List<String>,
    demandAlerts: List<DemandAlert>,
    competitorComparison: com.smartcart_merchant.features.dashboard.domain.model.CompetitorComparison?,
    visibilityRanking: com.smartcart_merchant.features.dashboard.domain.model.VisibilityRanking?,
    competitorRadiusM: Int,
    storeHours: List<OperatingHour>,
    storeOpenStatus: String,
    is24Hours: Boolean,
    weeklyOfferSuggestion: String?,
    trustProfile: TrustProfile,
    averageRating: Double,
    ratings: List<StoreRating>,
    onRefresh: () -> Unit,
    onEditHours: () -> Unit,
    onCompetitorRadiusChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bienvenido, $companyName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
                if (ruc.isNotBlank()) {
                    Text(
                        text = "RUC: $ruc",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            }
            DashboardActionButton(
                text = "Actualizar",
                onClick = onRefresh,
                containerColor = SmartBlue,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DashboardSectionCard(title = "Reputación de la tienda") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Confianza", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Text(
                        text = "${"%.0f".format(trustProfile.trustScore)}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SmartGreen
                    )
                }
                Column {
                    Text("Calificación", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Text(
                        text = if (averageRating > 0) "${"%.1f".format(averageRating)} / 5" else "Sin calificaciones",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }
                Column {
                    Text("Reseñas", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    Text(
                        text = trustProfile.totalRatings.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }
            }
            if (trustProfile.badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Insignias: ${trustProfile.badges.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )
            }
        }

        visibilityRanking?.let { ranking ->
            DashboardSectionCard(title = "Visibilidad de precios (US46)") {
                Text(ranking.message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1A1A2E))
                Text(
                    text = "Ranking #${ranking.rank} de ${ranking.totalStoresInDistrict} en ${ranking.district}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        competitorComparison?.let { comparison ->
            DashboardSectionCard(title = "Comparador de cadena (US31) — ${comparison.radiusMeters}m") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(250, 500, 1000).forEach { radius ->
                        FilterChip(
                            selected = competitorRadiusM == radius,
                            onClick = { onCompetitorRadiusChanged(radius) },
                            label = { Text("${radius}m") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Tu precio prom.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text("S/ ${"%.2f".format(comparison.storeAveragePrice)}", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Prom. zona", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text("S/ ${"%.2f".format(comparison.zoneAveragePrice)}", fontWeight = FontWeight.Bold)
                    }
                }
                if (comparison.savingsLeader && comparison.leaderBadge.isNotBlank()) {
                    Text(
                        text = "🏆 ${comparison.leaderBadge}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SmartGreen,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (comparison.competitors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    comparison.competitors.take(5).forEach { entry ->
                        Text(
                            text = "${entry.productName}: tú S/ ${"%.2f".format(entry.storePrice)} vs zona S/ ${"%.2f".format(entry.competitorPrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Actualizado: ${comparison.lastUpdated.take(16)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (demandAlerts.isNotEmpty()) {
            DashboardSectionCard(title = "Alertas de demanda (US25)") {
                demandAlerts.forEach { alert ->
                    Text(
                        text = "⚠ ${alert.productName} (${alert.sku}): ${alert.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        DashboardSectionCard(title = "Horarios de atención (US13)") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = storeOpenStatus,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (storeOpenStatus.contains("Abierto")) SmartGreen else Color(0xFF64748B)
                )
                if (is24Hours) {
                    Text(" 🌙 24h", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                }
            }
            if (storeHours.isEmpty()) {
                Text(
                    text = "No hay horarios registrados. Configúralos al registrar la sucursal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                storeHours.forEach { hour ->
                    Text(
                        text = "${hour.day}: ${hour.open} – ${hour.close}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            TextButton(onClick = onEditHours) {
                Text("Editar horarios especiales", color = SmartBlue)
            }
        }

        if (ratings.isNotEmpty()) {
            DashboardSectionCard(title = "Últimas calificaciones") {
                ratings.take(5).forEach { rating ->
                    Text(
                        text = "★ ${rating.score}/5 · ${rating.registeredAt.take(10)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        if (metrics.isEmpty()) {
            DashboardSectionCard(title = "Sin métricas disponibles") {
                Text(
                    text = "Carga tu catálogo y espera actividad de clientes para ver visitas, conversión y demanda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            Text(
                text = "Resumen de hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            metrics.chunked(2).forEachIndexed { rowIndex, rowMetrics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowMetrics.forEachIndexed { columnIndex, metric ->
                        val accent = accentColors()[(rowIndex * 2 + columnIndex) % accentColors().size]
                        DashboardMetricCard(
                            label = metric.label,
                            value = metric.value,
                            trend = metric.trend,
                            accentColor = accent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowMetrics.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        DashboardSectionCard(title = "Productos más buscados (Top 10)") {
            topDemandProducts.forEachIndexed { index, product ->
                Text(
                    text = "${index + 1}. $product",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        weeklyOfferSuggestion?.let { suggestion ->
            DashboardSectionCard(title = "Sugerencia de oferta (US25 E3)") {
                Text(suggestion, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
