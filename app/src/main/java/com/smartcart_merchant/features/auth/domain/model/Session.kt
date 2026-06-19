package com.smartcart_merchant.features.auth.domain.model

data class Session(
    val id: String,
    val username: String,
    val token: String,
    val roles: List<String>
)
