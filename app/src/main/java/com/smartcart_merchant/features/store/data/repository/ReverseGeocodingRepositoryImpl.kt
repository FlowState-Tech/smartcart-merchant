package com.smartcart_merchant.features.store.data.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.data.remote.api.NominatimApi
import com.smartcart_merchant.features.store.data.remote.dto.toDomain
import com.smartcart_merchant.features.store.domain.model.GeocodedAddress
import com.smartcart_merchant.features.store.domain.repository.ReverseGeocodingRepository
import javax.inject.Inject

class ReverseGeocodingRepositoryImpl @Inject constructor(
    private val api: NominatimApi
) : ReverseGeocodingRepository {

    override suspend fun reverseGeocode(latitude: Double, longitude: Double): Resource<GeocodedAddress> {
        return try {
            val response = api.reverseGeocode(lat = latitude, lon = longitude)
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener la dirección")
        }
    }
}
