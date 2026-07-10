package com.smartcart_merchant.features.store.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.smartcart_merchant.features.store.domain.model.GeocodedAddress

data class NominatimResponseDto(
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("address") val address: NominatimAddressDto?
)

data class NominatimAddressDto(
    @SerializedName("road") val road: String?,
    @SerializedName("suburb") val suburb: String?,
    @SerializedName("city_district") val cityDistrict: String?,
    @SerializedName("district") val district: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?
)

fun NominatimResponseDto.toDomain(): GeocodedAddress {
    val address = this.address
    val street = address?.road?.trim() ?: ""
    val district = address?.cityDistrict?.trim()
        ?: address?.district?.trim()
        ?: address?.suburb?.trim()
        ?: address?.city?.trim()
        ?: address?.town?.trim()
        ?: address?.village?.trim()
        ?: ""
    return GeocodedAddress(street = street, district = district)
}
