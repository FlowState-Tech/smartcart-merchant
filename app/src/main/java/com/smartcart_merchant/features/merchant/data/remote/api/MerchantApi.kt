package com.smartcart_merchant.features.merchant.data.remote.api

import com.smartcart_merchant.features.merchant.data.remote.dto.MerchantProfileResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface MerchantApi {
    @GET("merchants/me")
    suspend fun getProfile(): Response<MerchantProfileResponseDto>
}
