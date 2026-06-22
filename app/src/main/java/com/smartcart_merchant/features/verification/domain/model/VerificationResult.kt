package com.smartcart_merchant.features.verification.domain.model

data class VerificationResult(
    val applicationId: String,
    val merchantId: String,
    val ruc: String,
    val companyName: String,
    val status: VerificationStatus
)

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}
