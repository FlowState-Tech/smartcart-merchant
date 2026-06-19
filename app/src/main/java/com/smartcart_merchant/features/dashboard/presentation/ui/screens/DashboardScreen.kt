package com.smartcart_merchant.features.dashboard.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination
import com.smartcart_merchant.features.dashboard.presentation.ui.components.DashboardNavigationRail
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.CatalogManagementSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.DashboardHomeSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.OfferManagementSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.StoreQrSection
import com.smartcart_merchant.features.dashboard.presentation.ui.screens.sections.SupportSection
import com.smartcart_merchant.features.dashboard.presentation.viewmodel.DashboardViewModel

private val ContentBackground = Color(0xFFF5F5F5)

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        DashboardNavigationRail(
            selectedDestination = uiState.selectedDestination,
            onDestinationSelected = viewModel::onDestinationSelected,
            onLogout = onLogout,
            modifier = Modifier.fillMaxHeight()
        )

        DashboardContent(
            selectedDestination = uiState.selectedDestination,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        )
    }
}

@Composable
private fun DashboardContent(
    selectedDestination: DashboardDestination,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ContentBackground)
            .padding(24.dp)
    ) {
        Text(
            text = selectedDestination.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )

        when (selectedDestination) {
            DashboardDestination.DASHBOARD -> DashboardHomeSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.CATALOG_MANAGEMENT -> CatalogManagementSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.OFFER_MANAGEMENT -> OfferManagementSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.STORE_QR -> StoreQrSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            DashboardDestination.SUPPORT -> SupportSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
        }
    }
}
