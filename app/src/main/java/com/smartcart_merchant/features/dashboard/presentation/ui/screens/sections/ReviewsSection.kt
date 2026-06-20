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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.smartcart_merchant.features.dashboard.domain.model.StoreReview
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardSectionCard
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardActionButton
import com.smartcart_merchant.ui.theme.SmartBlue
import com.smartcart_merchant.ui.theme.SmartGreen

@Composable
fun ReviewsSection(
    reviews: List<StoreReview>,
    merchantId: String,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onReply: (reviewId: String, reply: String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var replyingTo by remember { mutableStateOf<StoreReview?>(null) }
    var replyText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Responde a las reseñas de tus clientes para mejorar la reputación de tu tienda.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        DashboardSectionCard(title = "Reseñas (${reviews.size})") {
            if (reviews.isEmpty()) {
                Text(
                    text = "Aún no hay reseñas. Cuando los clientes califiquen tu tienda, aparecerán aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                reviews.forEach { review ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = review.status.ifBlank { "PUBLICADA" },
                                style = MaterialTheme.typography.labelMedium,
                                color = SmartBlue
                            )
                            Text(
                                text = review.createdAt.take(10),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        Text(
                            text = review.comment,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        if (!review.reply.isNullOrBlank()) {
                            Text(
                                text = "Tu respuesta: ${review.reply}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SmartGreen,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            TextButton(onClick = {
                                replyingTo = review
                                replyText = ""
                            }) {
                                Text("Responder", color = SmartBlue)
                            }
                        }
                    }
                }
            }
            if (hasMore) {
                DashboardActionButton(
                    text = if (isLoadingMore) "Cargando..." else "Cargar más reseñas",
                    onClick = onLoadMore,
                    containerColor = SmartBlue,
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (replyingTo != null) {
        AlertDialog(
            onDismissRequest = { replyingTo = null },
            title = { Text("Responder reseña") },
            text = {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Tu respuesta (mín. 5 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (replyText.length >= 5) {
                            onReply(replyingTo!!.reviewId, replyText)
                            replyingTo = null
                        }
                    }
                ) { Text("Enviar") }
            },
            dismissButton = {
                TextButton(onClick = { replyingTo = null }) { Text("Cancelar") }
            }
        )
    }
}
