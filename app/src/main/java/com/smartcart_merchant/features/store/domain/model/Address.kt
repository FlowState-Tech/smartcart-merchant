package com.smartcart_merchant.features.store.domain.model

data class Address(
    val street: String,
    val district: String,
    val latitude: Double,
    val longitude: Double
)