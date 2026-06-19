package com.smartcart_merchant.features.auth.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthDestination {
    @Serializable
    data object SignIn : AuthDestination()

    @Serializable
    data object SignUp : AuthDestination()
}