package com.smartcart_merchant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.smartcart_merchant.BuildConfig
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcart_merchant.core.network.AuthEventBus
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignInScreen
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignUpScreen
import com.smartcart_merchant.features.verification.presentation.ui.screens.VerificationScreen
import com.smartcart_merchant.features.store.presentation.ui.screens.StoreScreen
import com.smartcart_merchant.ui.screens.MainDashboardScreen
import com.smartcart_merchant.ui.screens.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    AUTH,
    VERIFICATION,
    STORE_SETUP,
    DASHBOARD
}

private suspend fun resolveDestination(sessionPreferences: SessionPreferences): AppScreen {
    val hasSession = sessionPreferences.authToken.first() != null
    if (!hasSession) return AppScreen.AUTH

    val verified = sessionPreferences.isVerified.first()
    if (!verified) return AppScreen.VERIFICATION

    val storeId = sessionPreferences.storeId.first()
    if (storeId.isNullOrBlank()) return AppScreen.STORE_SETUP

    return AppScreen.DASHBOARD
}

@Composable
fun AppNavigation(
    sessionPreferences: SessionPreferences,
    authEventBus: AuthEventBus,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var currentScreen by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(AppScreen.SPLASH) }
    val isVerified by sessionPreferences.isVerified.collectAsState(initial = false)

    LaunchedEffect(isVerified) {
        if (currentScreen == AppScreen.SPLASH) {
            currentScreen = resolveDestination(sessionPreferences)
        }
    }

    LaunchedEffect(authEventBus) {
        authEventBus.sessionExpired.collect {
            scope.launch {
                sessionPreferences.clearSession()
                currentScreen = AppScreen.AUTH
            }
        }
    }

    when (currentScreen) {
        AppScreen.SPLASH -> {
            SplashScreen()
        }
        AppScreen.AUTH -> {
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
                            scope.launch {
                                currentScreen = resolveDestination(sessionPreferences)
                            }
                        }
                    )
                }

                composable("sign_up") {
                    SignUpScreen(
                        onNavigateToSignIn = {
                            navController.popBackStack()
                        },
                        onSignUpSuccess = {
                            scope.launch {
                                currentScreen = resolveDestination(sessionPreferences)
                            }
                        }
                    )
                }
            }
        }
        AppScreen.VERIFICATION -> {
            VerificationScreen(
                onVerificationSuccess = {
                    currentScreen = AppScreen.STORE_SETUP
                }
            )
        }
        AppScreen.STORE_SETUP -> {
            StoreScreen(
                onStoreSuccess = {
                    currentScreen = AppScreen.DASHBOARD
                },
                googleMapsApiKey = BuildConfig.MAPS_API_KEY
            )
        }
        AppScreen.DASHBOARD -> {
            MainDashboardScreen(
                onLogout = {
                    scope.launch {
                        sessionPreferences.clearSession()
                        currentScreen = AppScreen.AUTH
                    }
                }
            )
        }
    }
}
