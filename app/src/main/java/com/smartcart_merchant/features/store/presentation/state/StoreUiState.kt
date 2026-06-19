package com.smartcart_merchant.features.store.presentation.state

import com.smartcart_merchant.features.store.domain.model.OperatingHour

data class StoreUiState(
    val merchantId: String = "",
    val ruc: String = "",
    val storeName: String = "",
    val street: String = "",
    val district: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val operatingHours: List<OperatingHour> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class OperatingHourInput(
    val day: String = "",
    val openTime: String = "",
    val closeTime: String = ""
)