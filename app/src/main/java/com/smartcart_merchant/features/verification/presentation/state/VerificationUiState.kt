package com.smartcart_merchant.features.verification.presentation.state

import com.smartcart_merchant.features.verification.domain.model.VerificationStatus

data class VerificationUiState(
    val ruc: String = "",
    val status: VerificationStatus? = null,
    val companyName: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerified: Boolean = false
)
