package com.smartcart_merchant.features.store.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.data.remote.api.StoreApi
import com.smartcart_merchant.features.store.data.remote.dto.CreateStoreRequestDto
import com.smartcart_merchant.features.store.data.remote.dto.StoreResponseDto
import com.smartcart_merchant.features.store.domain.model.Store
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val api: StoreApi
) : StoreRepository {

    companion object {
        private const val TAG = "StoreRepository"
    }

    override suspend fun createStore(store: Store): Resource<Store> {
        return try {
            val request = CreateStoreRequestDto.fromDomain(store)
            val jsonRequest = Gson().toJson(request)
            Log.d(TAG, "=== STORE REQUEST ===")
            Log.d(TAG, jsonRequest)
            Log.d(TAG, "======================")

            val response = api.createStore(request)
            Log.d(TAG, "POST response code: ${response.code()}, body: ${response.body()}")

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "Error ${response.code()}: ${errorBody ?: response.message()}"
                Log.e(TAG, "HTTP error creating store: $errorMessage")
                return Resource.Error(errorMessage)
            }

            val storeId = extractStoreId(response.body())
            if (storeId == null) {
                Log.d(TAG, "Could not extract storeId, returning request store")
                return Resource.Success(store)
            }

            Log.d(TAG, "Created store id: $storeId, fetching details...")
            val detailResponse = api.getStoreById(storeId)
            Log.d(TAG, "GET response code: ${detailResponse.code()}, body: ${detailResponse.body()}")

            if (detailResponse.isSuccessful && detailResponse.body() != null) {
                Resource.Success(detailResponse.body()!!.toDomain(store.merchantId))
            } else {
                Log.w(TAG, "Failed to fetch store details, returning request store")
                Resource.Success(store)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating store: ${e.message}", e)
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun extractStoreId(body: JsonElement?): Long? {
        if (body == null || body.isJsonNull) return null
        return try {
            when {
                body.isJsonPrimitive -> body.asString.toLongOrNull()
                body.isJsonObject -> {
                    val obj = body.asJsonObject
                    val id = obj.get("storeId") ?: obj.get("id")
                    id?.asString?.toLongOrNull()
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting storeId: ${e.message}")
            null
        }
    }
}