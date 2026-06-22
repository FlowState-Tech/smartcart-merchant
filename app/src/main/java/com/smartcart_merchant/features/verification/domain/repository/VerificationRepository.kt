package com.smartcart_merchant.features.verification.domain.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.verification.domain.model.VerificationResult

interface VerificationRepository {
    suspend fun verifyRuc(merchantId: String, ruc: String): Resource<VerificationResult>
}
