package com.smartcart_merchant.features.dashboard.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartcart_merchant.R
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination

private val DarkBlue = Color(0xFF080D54)
private val BrightBlue = Color(0xFF4EA8DE)

@Composable
fun DashboardNavigationRail(
    selectedDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(80.dp)
            .background(DarkBlue)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmartCartLogoItem()

        Spacer(modifier = Modifier.height(32.dp))

        DashboardDestination.entries.forEach { destination ->
            NavItem(
                destination = destination,
                isSelected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LogoutItem(onLogout = onLogout)
    }
}

@Composable
private fun NavItem(
    destination: DashboardDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BrightBlue else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(destination.icon),
            contentDescription = destination.contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun LogoutItem(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .clickable(onClick = onLogout),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(DashboardDestination.logoutIcon),
            contentDescription = "Cerrar sesión",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SmartCartLogoItem() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.smartcart_logo),
            contentDescription = "Logo SmartCart",
            modifier = Modifier
                .size(44.dp)
                .padding(1.dp),
            contentScale = ContentScale.Fit
        )
    }
}
