package com.smartcart_merchant.features.store.domain.model

data class Store(
    val merchantId: String,
    val name: String,
    val ruc: String,
    val address: Address,
    val operatingHours: List<OperatingHour>,
    val id: String? = null
)