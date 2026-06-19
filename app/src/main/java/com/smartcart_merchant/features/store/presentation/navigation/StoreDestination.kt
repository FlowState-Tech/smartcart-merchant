package com.smartcart_merchant.features.store.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class StoreDestination {
    @Serializable
    data object StoreSetup : StoreDestination()
}