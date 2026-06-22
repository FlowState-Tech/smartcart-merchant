package com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.features.dashboard.domain.model.PriceErrorItem
import com.smartcart_merchant.features.dashboard.presentation.state.SupportTopic
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun SupportSection(
    topics: List<SupportTopic>,
    priceErrors: List<PriceErrorItem>,
    onReportLocation: () -> Unit,
    onContactSupport: () -> Unit,
    onConfirmPriceError: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¿Necesitas ayuda con tu tienda, catálogo o ubicación en el mapa?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        DashboardSectionCard(title = "Errores de precio reportados") {
            val pending = priceErrors.filter {
                it.status.equals("PENDIENTE", ignoreCase = true) ||
                    it.status.equals("REPORTADO", ignoreCase = true)
            }
            if (pending.isEmpty()) {
                Text(
                    text = "No hay errores de precio pendientes de revisión.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                pending.forEach { error ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Producto ${error.productId}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                text = "Discrepancia: S/ ${"%.2f".format(error.discrepancy)} · ${error.status}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        Row {
                            TextButton(onClick = { onConfirmPriceError(error.errorId, true) }) {
                                Text("Confirmar", color = SmartGreen)
                            }
                            TextButton(onClick = { onConfirmPriceError(error.errorId, false) }) {
                                Text("Rechazar", color = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }

        DashboardSectionCard(title = "Preguntas frecuentes") {
            topics.forEach { topic ->
                FaqItem(question = topic.question, answer = topic.answer)
            }
        }

        DashboardSectionCard(title = "Reportar un problema") {
            Text(
                text = "Si la ubicación de tu tienda es incorrecta o detectas un error en el panel, genera un ticket de soporte.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(12.dp))
            DashboardActionButton(
                text = "Reportar ubicación incorrecta",
                onClick = onReportLocation,
                modifier = Modifier.fillMaxWidth(),
                containerColor = SmartBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            DashboardActionButton(
                text = "Contactar soporte",
                onClick = onContactSupport,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF475569)
            )
        }

        DashboardSectionCard(title = "Contacto") {
            Text(
                text = "soporte@flowstatetech.com",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A2E)
            )
            Text(
                text = "Horario: Lun–Vie, 9:00 a.m. – 6:00 p.m.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E)
        )
        Text(
            text = if (expanded) answer else "Ver respuesta",
            style = MaterialTheme.typography.bodyMedium,
            color = if (expanded) Color(0xFF475569) else SmartBlue,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
