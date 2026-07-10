package com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.core.util.QrCodeGenerator
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardInfoRow
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.ui.theme.SmartBlue

@Composable
fun StoreQrSection(
    companyName: String,
    storeId: String,
    qrLandingUrl: String,
    scanCount: String,
    onDownloadQr: () -> Unit,
    onPrintQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    val qrBitmap = remember(qrLandingUrl) {
        if (qrLandingUrl.isNotBlank()) {
            QrCodeGenerator.generateBitmap(qrLandingUrl, 512)
        } else {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Comparte el QR en caja para que los clientes descarguen SmartCart.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        DashboardSectionCard(title = "Kit de tienda") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(2.dp, SmartBlue.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Código QR de la tienda",
                            modifier = Modifier
                                .size(180.dp)
                                .padding(8.dp)
                        )
                    } else {
                        Text(
                            text = "Registra tu sucursal para generar el QR",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Text(
                    text = companyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E),
                    textAlign = TextAlign.Center
                )

                if (storeId.isNotBlank()) {
                    Text(
                        text = "ID local: $storeId",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }

                if (qrLandingUrl.isNotBlank()) {
                    Text(
                        text = qrLandingUrl,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardActionButton(
                        text = "Compartir QR",
                        onClick = onDownloadQr,
                        modifier = Modifier.weight(1f),
                        containerColor = SmartBlue,
                        enabled = qrBitmap != null
                    )
                    DashboardActionButton(
                        text = "Abrir landing",
                        onClick = onPrintQr,
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFF475569),
                        enabled = qrLandingUrl.isNotBlank()
                    )
                }
            }
        }

        DashboardSectionCard(title = "Métricas de alcance") {
            DashboardInfoRow(label = "Visitas landing QR", value = scanCount)
            Spacer(modifier = Modifier.height(8.dp))
            DashboardInfoRow(label = "Landing vinculada", value = if (qrLandingUrl.isBlank()) "No disponible" else "Activa")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
