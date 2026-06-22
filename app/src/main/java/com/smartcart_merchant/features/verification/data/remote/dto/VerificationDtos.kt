package com.smartcart_merchant.features.verification.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VerificationRequestDto(
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("ruc") val ruc: String
)

data class VerificationResponseDto(
    @SerializedName("applicationId") val applicationId: Long,
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("ruc") val ruc: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("status") val status: String
)

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}
