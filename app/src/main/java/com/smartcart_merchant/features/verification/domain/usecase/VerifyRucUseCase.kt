package com.smartcart_merchant.features.verification.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.verification.domain.model.VerificationResult
import com.smartcart_merchant.features.verification.domain.repository.VerificationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyRucUseCase @Inject constructor(
    private val repository: VerificationRepository,
    private val sessionPreferences: SessionPreferences
) {
    suspend operator fun invoke(ruc: String): Resource<VerificationResult> {
        val merchantId = sessionPreferences.merchantId.first()
            ?: return Resource.Error("Merchant ID not found. Please sign in again.")

        if (ruc.isBlank()) {
            return Resource.Error("Please enter a RUC")
        }

        val result = repository.verifyRuc(merchantId, ruc)

        if (result is Resource.Success && result.data != null) {
            val verification = result.data
            if (verification.status == com.smartcart_merchant.features.verification.domain.model.VerificationStatus.VERIFIED) {
                sessionPreferences.saveVerificationStatus(
                    applicationId = verification.applicationId,
                    companyName = verification.companyName
                )
            }
        }

        return result
    }
}
