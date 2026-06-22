package com.smartcart_merchant.features.store.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.data.remote.api.StoreApi
import com.smartcart_merchant.features.store.data.remote.dto.CreateStoreRequestDto
import com.smartcart_merchant.features.store.domain.model.Store
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val api: StoreApi
) : StoreRepository {

    companion object {
        private const val TAG = "StoreRepository"
    }

    override suspend fun getStore(storeId: Long): Resource<Store> {
        return try {
            val response = api.getStoreById(storeId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain("").copy(id = storeId.toString()))
            } else {
                Resource.Error("Error ${response.code()}: ${response.errorBody()?.string() ?: response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getStore failed", e)
            Resource.Error(e.message ?: "No se pudo cargar la tienda")
        }
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
                Log.e(TAG, "Could not extract storeId from response")
                return Resource.Error("No se pudo obtener el ID de la tienda creada")
            }

            Log.d(TAG, "Created store id: $storeId, fetching details...")
            val detailResponse = api.getStoreById(storeId)
            Log.d(TAG, "GET response code: ${detailResponse.code()}, body: ${detailResponse.body()}")

            if (detailResponse.isSuccessful && detailResponse.body() != null) {
                Resource.Success(detailResponse.body()!!.toDomain(store.merchantId).copy(id = storeId.toString()))
            } else {
                Log.w(TAG, "Failed to fetch store details, returning store with id only")
                Resource.Success(store.copy(id = storeId.toString()))
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
                body.isJsonPrimitive -> {
                    val primitive = body.asJsonPrimitive
                    when {
                        primitive.isNumber -> primitive.asLong
                        primitive.isString -> primitive.asString.toLongOrNull()
                        else -> null
                    }
                }
                body.isJsonObject -> {
                    val obj = body.asJsonObject
                    val id = obj.get("storeId") ?: obj.get("id")
                    when {
                        id == null || id.isJsonNull -> null
                        id.isJsonPrimitive && id.asJsonPrimitive.isNumber -> id.asLong
                        else -> id.asString.toLongOrNull()
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting storeId: ${e.message}")
            null
        }
    }
}
