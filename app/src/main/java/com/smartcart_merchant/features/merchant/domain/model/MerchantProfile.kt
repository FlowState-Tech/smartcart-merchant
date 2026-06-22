package com.smartcart_merchant.features.merchant.domain.model

data class MerchantProfile(
    val id: Long,
    val username: String,
    val isVerified: Boolean,
    val applicationId: Long?,
    val companyName: String?,
    val ruc: String?,
    val stores: List<MerchantStore>
)

data class MerchantStore(
    val storeId: Long,
    val name: String
)
