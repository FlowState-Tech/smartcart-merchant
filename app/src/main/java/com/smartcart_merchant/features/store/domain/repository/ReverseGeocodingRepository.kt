package com.smartcart_merchant.features.store.domain.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.domain.model.GeocodedAddress

interface ReverseGeocodingRepository {

    suspend fun reverseGeocode(latitude: Double, longitude: Double): Resource<GeocodedAddress>
}
