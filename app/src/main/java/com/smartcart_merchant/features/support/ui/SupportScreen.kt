package com.smartcart_merchant.features.support.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SupportScreen() {
    val tickets = listOf(
        SupportTicketUi("#1234", "Error al actualizar precios", "En proceso"),
        SupportTicketUi("#1233", "QR no se genera", "Resuelto"),
        SupportTicketUi("#1232", "Problema con catálogo", "Pendiente"),
        SupportTicketUi("#1231", "Consulta sobre ofertas", "Resuelto")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Soporte Tecnico",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReportFormCard(modifier = Modifier.weight(1.1f))
            SupportSidePanel(modifier = Modifier.weight(0.9f), tickets = tickets)
        }
    }
}

private data class SupportTicketUi(
    val id: String,
    val title: String,
    val status: String
)

@Composable
private fun ReportFormCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Reportar un problema", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Asunto", fontSize = 12.sp, color = Color(0xFF6B7280))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Describe brevemente el problema") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categoria", fontSize = 12.sp, color = Color(0xFF6B7280))
                OutlinedTextField(
                    value = "Seleccionar tipo de problema",
                    onValueChange = {},
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Descripcion detallada", fontSize = 12.sp, color = Color(0xFF6B7280))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Explica el problema con el mayor detalle posible...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Adjuntar captura", fontSize = 12.sp, color = Color(0xFF6B7280))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null, tint = Color(0xFF2563EB))
                        Spacer(Modifier.width(8.dp))
                        Text("Subir archivo", color = Color(0xFF2563EB), fontSize = 12.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Enviar reporte")
                }
            }
        }
    }
}

@Composable
private fun SupportSidePanel(modifier: Modifier = Modifier, tickets: List<SupportTicketUi>) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Tickets recientes", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                tickets.forEach { ticket ->
                    TicketItem(ticket)
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Contacto directo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                ContactRow(Icons.Filled.Email, "soporte@smartcart.com")
                ContactRow(Icons.Filled.Phone, "+51 999 999 999")
            }
        }
    }
}

@Composable
private fun TicketItem(ticket: SupportTicketUi) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ticket.id, fontSize = 11.sp, color = Color(0xFF9CA3AF))
                Spacer(Modifier.weight(1f))
                StatusChip(ticket.status)
            }
            Text(ticket.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("Hace 2 horas", fontSize = 11.sp, color = Color(0xFF9CA3AF))
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "En proceso" -> Color(0xFFFFEDD5) to Color(0xFFEA580C)
        "Resuelto" -> Color(0xFFD1FAE5) to Color(0xFF10B981)
        else -> Color(0xFFFEE2E2) to Color(0xFFEF4444)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(status, fontSize = 10.sp, color = fg)
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2563EB))
        Spacer(Modifier.width(8.dp))
        Text(value, fontSize = 12.sp, color = Color(0xFF2563EB))
    }
}
