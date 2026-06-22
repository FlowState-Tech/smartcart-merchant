package com.smartcart_merchant.features.merchant.data.repository

import android.util.Log
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.merchant.data.remote.api.MerchantApi
import com.smartcart_merchant.features.merchant.domain.model.MerchantProfile
import com.smartcart_merchant.features.merchant.domain.repository.MerchantRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MerchantRepositoryImpl"

@Singleton
class MerchantRepositoryImpl @Inject constructor(
    private val api: MerchantApi
) : MerchantRepository {

    override suspend fun getProfile(): Resource<MerchantProfile> {
        return try {
            val response = api.getProfile()
            when {
                response.isSuccessful && response.body() != null -> {
                    Resource.Success(response.body()!!.toDomain())
                }
                else -> {
                    val errorMsg = "Failed to load merchant profile: ${response.code()} ${response.message()}"
                    Log.e(TAG, errorMsg)
                    Resource.Error(errorMsg)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getProfile failed", e)
            Resource.Error(e.message ?: "No se pudo cargar el perfil del comerciante")
        }
    }
}
