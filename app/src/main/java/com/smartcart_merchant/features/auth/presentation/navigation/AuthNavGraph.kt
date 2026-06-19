package com.smartcart_merchant.features.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignInScreen
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    navigation<AuthDestination.SignIn>(
        startDestination = AuthDestination.SignIn
    ) {
        composable<AuthDestination.SignIn> {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(AuthDestination.SignUp)
                },
                onLoginSuccess = onLoginSuccess
            )
        }
        composable<AuthDestination.SignUp> {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }
    }
}