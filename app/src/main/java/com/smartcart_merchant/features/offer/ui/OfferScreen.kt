package com.smartcart_merchant.features.offer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OffersScreen() {
    val offers = listOf(
        OfferUi("2x1", "Oferta 1", "Promocion especial en productos...", "15/05/2026", 0.6f),
        OfferUi("20% OFF", "Oferta 2", "Promocion especial en productos...", "15/05/2026", 0.8f),
        OfferUi("2x1", "Oferta 3", "Promocion especial en productos...", "15/05/2026", 0.5f),
        OfferUi("20% OFF", "Oferta 4", "Promocion especial en productos...", "15/05/2026", 0.7f),
        OfferUi("2x1", "Oferta 5", "Promocion especial en productos...", "15/05/2026", 0.4f),
        OfferUi("20% OFF", "Oferta 6", "Promocion especial en productos...", "15/05/2026", 0.3f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OffersHeader()

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(offers) { offer ->
                OfferCard(offer)
            }
        }
    }
}

private data class OfferUi(
    val badge: String,
    val title: String,
    val description: String,
    val validUntil: String,
    val progress: Float
)

@Composable
private fun OffersHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gestion de Ofertas",
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
            Text("Crear oferta")
        }
    }
}

@Composable
private fun OfferCard(offer: OfferUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD9F99D))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(offer.badge, fontSize = 11.sp, color = Color(0xFF365314))
                }
                Spacer(Modifier.weight(1f))
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null, tint = Color(0xFF9CA3AF))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.LocalOffer, contentDescription = null, tint = Color(0xFFF97316))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(offer.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    offer.description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Timer, contentDescription = null, tint = Color(0xFF9CA3AF))
                Spacer(Modifier.width(6.dp))
                Text("Valido hasta", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                Spacer(Modifier.width(6.dp))
                Text(offer.validUntil, fontSize = 11.sp, color = Color(0xFF111827))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Timer, contentDescription = null, tint = Color(0xFF3B82F6))
                Spacer(Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { offer.progress },
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth(),
                    color = Color(0xFF3B82F6),
                    trackColor = Color(0xFFE5E7EB)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(10.dp)) {
                    Text("Editar", fontSize = 12.sp)
                }
                Button(onClick = {}, shape = RoundedCornerShape(10.dp)) {
                    Text("Ver detalles", fontSize = 12.sp)
                }
            }
        }
    }
}
