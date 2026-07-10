package com.smartcart_merchant.features.store.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.smartcart_merchant.features.store.presentation.ui.screens.StoreScreen

fun NavGraphBuilder.storeNavGraph(
    navController: NavController,
    onStoreSuccess: () -> Unit
) {
    navigation<StoreDestination.StoreSetup>(
        startDestination = StoreDestination.StoreSetup
    ) {
        composable<StoreDestination.StoreSetup> {
            StoreScreen(
                onStoreSuccess = onStoreSuccess
            )
        }
    }
}