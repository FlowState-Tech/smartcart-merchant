package com.smartcart_merchant.features.store.data.remote.api

import com.google.gson.JsonElement
import com.smartcart_merchant.features.store.data.remote.dto.CreateStoreRequestDto
import com.smartcart_merchant.features.store.data.remote.dto.StoreDetailResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoreApi {
    @POST("store-management/stores")
    suspend fun createStore(@Body request: CreateStoreRequestDto): Response<JsonElement>

    @GET("store-management/stores/{storeId}")
    suspend fun getStoreById(@Path("storeId") storeId: Long): Response<StoreDetailResponseDto>
}