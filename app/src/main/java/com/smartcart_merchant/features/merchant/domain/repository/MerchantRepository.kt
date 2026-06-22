package com.smartcart_merchant.features.merchant.domain.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.merchant.domain.model.MerchantProfile

interface MerchantRepository {
    suspend fun getProfile(): Resource<MerchantProfile>
}
