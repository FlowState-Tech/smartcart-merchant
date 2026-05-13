package com.smartcart_merchant.features.qr.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable

@Composable
fun QrScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QrHeader()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QrCard(
                modifier = Modifier
                    .weight(1.2f)
            )
            RightColumn(
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun QrHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Codigo QR de tu Tienda",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = {}, shape = RoundedCornerShape(10.dp)) {
            Text("Descargar PDF", fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text("Visible en app", fontSize = 12.sp, color = Color(0xFF6B7280))
            Switch(checked = true, onCheckedChange = {})
        }
    }
}

@Composable
private fun QrCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                QrPlaceholderGrid()
            }
            Text("Plaza Vea Centro", fontWeight = FontWeight.SemiBold)
            Text("Codigo de tienda: #PVC001", fontSize = 12.sp, color = Color(0xFF9CA3AF))
        }
    }
}

@Composable
private fun QrPlaceholderGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(6) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF111827))
                    )
                }
            }
        }
    }
}

@Composable
private fun RightColumn(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StatsCard()
        AlertsCard()
        Button(
            onClick = {},
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Descargar PNG")
        }
    }
}

@Composable
private fun StatsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Estadisticas", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                StatItem("Escaneos hoy", "45")
                StatItem("Esta semana", "312")
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                StatItem("Este mes", "1,248")
                StatItem("Total", "12,450")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, color = Color(0xFF9CA3AF))
        Text(value, fontSize = 16.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AlertsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Alertas", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            AlertRow(Icons.Filled.Notifications, "Notificar cuando escaneen", true)
            AlertRow(Icons.Filled.Assessment, "Reporte diario", true)
            AlertRow(Icons.Filled.Warning, "Alertas de stock", false)
        }
    }
}

@Composable
private fun AlertRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, checked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF3B82F6))
        }
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = {})
    }
}
