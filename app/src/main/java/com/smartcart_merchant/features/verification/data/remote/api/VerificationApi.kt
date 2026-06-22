package com.smartcart_merchant.features.verification.data.remote.api

import com.smartcart_merchant.features.verification.data.remote.dto.VerificationRequestDto
import com.smartcart_merchant.features.verification.data.remote.dto.VerificationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VerificationApi {
    @POST("verification/applications")
    suspend fun verifyRuc(@Body request: VerificationRequestDto): Response<VerificationResponseDto>
}
