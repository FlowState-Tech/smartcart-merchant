package com.smartcart_merchant.features.merchant.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.smartcart_merchant.features.merchant.domain.model.MerchantProfile
import com.smartcart_merchant.features.merchant.domain.model.MerchantStore

data class MerchantProfileResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("isVerified") val isVerified: Boolean,
    @SerializedName("applicationId") val applicationId: Long?,
    @SerializedName("companyName") val companyName: String?,
    @SerializedName("ruc") val ruc: String?,
    @SerializedName("stores") val stores: List<MerchantStoreDto>?
) {
    fun toDomain(): MerchantProfile = MerchantProfile(
        id = id,
        username = username,
        isVerified = isVerified,
        applicationId = applicationId,
        companyName = companyName,
        ruc = ruc,
        stores = stores?.map { it.toDomain() }.orEmpty()
    )
}

data class MerchantStoreDto(
    @SerializedName("storeId") val storeId: Long,
    @SerializedName("name") val name: String
) {
    fun toDomain(): MerchantStore = MerchantStore(
        storeId = storeId,
        name = name
    )
}
