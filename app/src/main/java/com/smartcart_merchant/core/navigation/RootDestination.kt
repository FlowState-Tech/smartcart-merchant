package com.smartcart_merchant.core.navigation
import kotlinx.serialization.Serializable

sealed interface RootDestination {
    @Serializable
    data object Splash : RootDestination

    @Serializable
    data object VerificationFlow : RootDestination

    @Serializable
    data object StoreSetupFlow : RootDestination

    @Serializable
    data object MainDashboardFlow : RootDestination
}