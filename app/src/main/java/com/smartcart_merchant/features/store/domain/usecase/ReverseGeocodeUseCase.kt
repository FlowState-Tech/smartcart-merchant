package com.smartcart_merchant.features.store.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.domain.model.GeocodedAddress
import com.smartcart_merchant.features.store.domain.repository.ReverseGeocodingRepository
import javax.inject.Inject

class ReverseGeocodeUseCase @Inject constructor(
    private val repository: ReverseGeocodingRepository
) {

    suspend operator fun invoke(latitude: Double, longitude: Double): Resource<GeocodedAddress> {
        return repository.reverseGeocode(latitude, longitude)
    }
}
