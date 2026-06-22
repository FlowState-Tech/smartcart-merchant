package com.smartcart_merchant.features.auth.presentation.state

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false,
    val passwordVisible: Boolean = false
)
