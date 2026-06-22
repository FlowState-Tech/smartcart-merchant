package com.smartcart_merchant.features.auth.presentation.state

data class SignUpUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false
)