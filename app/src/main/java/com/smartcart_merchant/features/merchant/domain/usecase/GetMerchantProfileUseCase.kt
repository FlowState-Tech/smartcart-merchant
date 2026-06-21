package com.smartcart_merchant.features.merchant.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.merchant.domain.model.MerchantProfile
import com.smartcart_merchant.features.merchant.domain.repository.MerchantRepository
import javax.inject.Inject

class GetMerchantProfileUseCase @Inject constructor(
    private val repository: MerchantRepository
) {
    suspend operator fun invoke(): Resource<MerchantProfile> {
        return repository.getProfile()
    }
}
