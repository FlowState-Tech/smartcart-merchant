package com.smartcart_merchant.features.verification.data.repository

import android.util.Log
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.verification.data.remote.api.VerificationApi
import com.smartcart_merchant.features.verification.data.remote.dto.VerificationRequestDto
import com.smartcart_merchant.features.verification.data.remote.dto.VerificationStatus as DtoVerificationStatus
import com.smartcart_merchant.features.verification.domain.model.VerificationResult
import com.smartcart_merchant.features.verification.domain.model.VerificationStatus
import com.smartcart_merchant.features.verification.domain.repository.VerificationRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VerificationRepository"

@Singleton
class VerificationRepositoryImpl @Inject constructor(
    private val api: VerificationApi
) : VerificationRepository {
    override suspend fun verifyRuc(merchantId: String, ruc: String): Resource<VerificationResult> {
        return try {
            Log.d(TAG, "Attempting verification for merchant: $merchantId with RUC: $ruc")
            val response = api.verifyRuc(VerificationRequestDto(merchantId, ruc))
            Log.d(TAG, "Response received - code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")
                if (body != null) {
                    val status = when (body.status.uppercase()) {
                        "VERIFIED" -> VerificationStatus.VERIFIED
                        "REJECTED" -> VerificationStatus.REJECTED
                        else -> VerificationStatus.PENDING
                    }
                    val result = VerificationResult(
                        applicationId = body.applicationId.toString(),
                        merchantId = body.merchantId,
                        ruc = body.ruc,
                        companyName = body.companyName,
                        status = status
                    )
                    Log.d(TAG, "Verification successful with status: ${result.status}")
                    Resource.Success(result)
                } else {
                    Log.e(TAG, "Empty response body from server")
                    Resource.Error("Empty response from server")
                }
            } else {
                val errorMsg = "Verification failed: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Verification exception: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Unknown error: ${e.message}")
        }
    }
}
