package com.smartcart_merchant.features.store.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.smartcart_merchant.features.store.domain.model.Address
import com.smartcart_merchant.features.store.domain.model.OperatingHour
import com.smartcart_merchant.features.store.domain.model.Store

data class AddressDto(
    @SerializedName("street") val street: String,
    @SerializedName("district") val district: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
) {
    fun toDomain() = Address(street, district, latitude, longitude)

    companion object {
        fun fromDomain(address: Address) = AddressDto(
            street = address.street,
            district = address.district,
            latitude = address.latitude,
            longitude = address.longitude
        )
    }
}

data class OperatingHourDto(
    @SerializedName("day") val day: String,
    @SerializedName("open") val open: String,
    @SerializedName("close") val close: String
) {
    fun toDomain() = OperatingHour(day, open, close)

    companion object {
        fun fromDomain(hour: OperatingHour) = OperatingHourDto(
            day = hour.day,
            open = hour.open,
            close = hour.close
        )
    }
}

data class CreateStoreRequestDto(
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("name") val name: String,
    @SerializedName("ruc") val ruc: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("operatingHours") val operatingHours: List<OperatingHourDto>
) {
    companion object {
        fun fromDomain(store: Store) = CreateStoreRequestDto(
            merchantId = store.merchantId,
            name = store.name,
            ruc = store.ruc,
            address = AddressDto.fromDomain(store.address),
            operatingHours = store.operatingHours.map { OperatingHourDto.fromDomain(it) }
        )
    }
}

data class StoreResponseDto(
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("name") val name: String,
    @SerializedName("ruc") val ruc: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("operatingHours") val operatingHours: List<OperatingHourDto>
) {
    fun toDomain() = Store(
        merchantId = merchantId,
        name = name,
        ruc = ruc,
        address = address.toDomain(),
        operatingHours = operatingHours.map { it.toDomain() }
    )
}

data class MerchantDto(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("dni") val dni: String,
    @SerializedName("email") val email: String,
    @SerializedName("lastLogin") val lastLogin: String
)

data class BranchDto(
    @SerializedName("address") val address: AddressDto,
    @SerializedName("openingHours") val openingHours: List<OperatingHourDto>,
    @SerializedName("active") val active: Boolean
)

data class StoreDetailResponseDto(
    @SerializedName("storeId") val storeId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("ruc") val ruc: String,
    @SerializedName("merchant") val merchant: MerchantDto,
    @SerializedName("branches") val branches: List<BranchDto>
) {
    fun toDomain(merchantId: String): Store {
        val firstBranch = branches.firstOrNull()
        return Store(
            merchantId = merchantId,
            name = name,
            ruc = ruc,
            address = firstBranch?.address?.toDomain() ?: Address("", "", 0.0, 0.0),
            operatingHours = firstBranch?.openingHours?.map { it.toDomain() } ?: emptyList(),
            id = storeId.toString()
        )
    }
}