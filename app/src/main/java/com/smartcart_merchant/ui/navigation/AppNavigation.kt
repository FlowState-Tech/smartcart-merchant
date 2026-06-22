package com.smartcart_merchant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.smartcart_merchant.BuildConfig
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcart_merchant.core.network.AuthEventBus
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignInScreen
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignUpScreen
import com.smartcart_merchant.features.merchant.presentation.viewmodel.SplashDestination
import com.smartcart_merchant.features.merchant.presentation.viewmodel.SplashViewModel
import com.smartcart_merchant.features.verification.presentation.ui.screens.VerificationScreen
import com.smartcart_merchant.features.store.presentation.ui.screens.StoreScreen
import com.smartcart_merchant.ui.screens.MainDashboardScreen
import com.smartcart_merchant.ui.screens.SplashScreen

@Composable
fun AppNavigation(
    authEventBus: AuthEventBus,
    modifier: Modifier = Modifier,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val destination by splashViewModel.destination.collectAsState()

    LaunchedEffect(authEventBus) {
        authEventBus.sessionExpired.collect {
            splashViewModel.onLogout()
        }
    }

    when (destination) {
        SplashDestination.Loading -> {
            SplashScreen()
        }
        SplashDestination.Auth -> {
            NavHost(
                navController = navController,
                startDestination = "sign_in"
            ) {
                composable("sign_in") {
                    SignInScreen(
                        onNavigateToSignUp = {
                            navController.navigate("sign_up")
                        },
                        onLoginSuccess = {
                            splashViewModel.loadDestination()
                        }
                    )
                }

                composable("sign_up") {
                    SignUpScreen(
                        onNavigateToSignIn = {
                            navController.popBackStack()
                        },
                        onSignUpSuccess = {
                            splashViewModel.loadDestination()
                        }
                    )
                }
            }
        }
        SplashDestination.Verification -> {
            VerificationScreen(
                onVerificationSuccess = {
                    splashViewModel.loadDestination()
                }
            )
        }
        SplashDestination.StoreSetup -> {
            StoreScreen(
                onStoreSuccess = {
                    splashViewModel.loadDestination()
                },
                googleMapsApiKey = BuildConfig.MAPS_API_KEY
            )
        }
        SplashDestination.Dashboard -> {
            MainDashboardScreen(
                onLogout = {
                    splashViewModel.onLogout()
                }
            )
        }
    }
}
