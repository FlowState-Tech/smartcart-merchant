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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.features.dashboard.domain.model.NotificationHistoryItem
import com.smartcart_merchant.features.dashboard.domain.model.NotificationPreferences
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun NotificationsSection(
    preferences: NotificationPreferences,
    history: List<NotificationHistoryItem>,
    isSaving: Boolean,
    isSendingTest: Boolean,
    hasMoreHistory: Boolean,
    isLoadingMore: Boolean,
    onToggleChannel: (String, Boolean) -> Unit,
    onSilenceWindowChanged: (String, String) -> Unit,
    onSavePreferences: () -> Unit,
    onSendTest: (String) -> Unit,
    onLoadMore: () -> Unit,
    visibilityAlertsEnabled: Boolean,
    onToggleVisibilityAlerts: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configura cómo quieres recibir alertas de stock, ofertas y actividad de clientes.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        DashboardSectionCard(title = "Canales de comunicación") {
            preferences.channels.forEach { channel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = channelLabel(channel.type),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(
                            text = if (channel.enabled) "Activo" else "Desactivado",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    Switch(
                        checked = channel.enabled,
                        onCheckedChange = { onToggleChannel(channel.type, it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DashboardActionButton(
                text = if (isSaving) "Guardando..." else "Guardar preferencias",
                onClick = onSavePreferences,
                containerColor = SmartBlue,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        }

        DashboardSectionCard(title = "Alertas de visibilidad (US46)") {
            Text(
                text = "Recibe felicitaciones cuando seas el más barato del distrito y avisos si pierdes el liderazgo.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alertas de ranking de precios", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = visibilityAlertsEnabled, onCheckedChange = onToggleVisibilityAlerts)
            }
        }

        DashboardSectionCard(title = "Ventana de silencio") {
            Text(
                text = "No recibirás notificaciones push durante este horario.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = preferences.silenceStart.orEmpty(),
                onValueChange = { onSilenceWindowChanged(it, preferences.silenceEnd.orEmpty()) },
                label = { Text("Inicio (HH:mm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = preferences.silenceEnd.orEmpty(),
                onValueChange = { onSilenceWindowChanged(preferences.silenceStart.orEmpty(), it) },
                label = { Text("Fin (HH:mm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        DashboardSectionCard(title = "Probar notificaciones") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardActionButton(
                    text = if (isSendingTest) "Enviando..." else "Test PUSH",
                    onClick = { onSendTest("PUSH") },
                    containerColor = SmartGreen,
                    enabled = !isSendingTest,
                    modifier = Modifier.weight(1f)
                )
                DashboardActionButton(
                    text = "Test EMAIL",
                    onClick = { onSendTest("EMAIL") },
                    containerColor = SmartBlue,
                    enabled = !isSendingTest,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        DashboardSectionCard(title = "Historial (${history.size})") {
            if (history.isEmpty()) {
                Text(
                    text = "No hay notificaciones enviadas todavía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                history.forEach { item ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = item.subject.ifBlank { "Notificación ${item.channel}" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(
                            text = "${item.summary} · ${item.status}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                if (hasMoreHistory) {
                    DashboardActionButton(
                        text = if (isLoadingMore) "Cargando..." else "Cargar más",
                        onClick = onLoadMore,
                        containerColor = SmartBlue,
                        enabled = !isLoadingMore,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (isLoadingMore) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

private fun channelLabel(type: String): String = when (type.uppercase()) {
    "PUSH" -> "Notificaciones push"
    "EMAIL" -> "Correo electrónico"
    "SMS" -> "Mensajes SMS"
    else -> type
}
