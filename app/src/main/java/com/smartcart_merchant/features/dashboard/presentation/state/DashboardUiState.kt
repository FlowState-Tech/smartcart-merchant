package com.smartcart_merchant.features.dashboard.presentation.state

import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination

data class DashboardUiState(
    val selectedDestination: DashboardDestination = DashboardDestination.DASHBOARD
)
