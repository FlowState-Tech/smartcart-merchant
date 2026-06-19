package com.smartcart_merchant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignInScreen
import com.smartcart_merchant.features.auth.presentation.ui.screens.SignUpScreen
import com.smartcart_merchant.ui.theme.SmartcartmerchantTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionPreferences: SessionPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartcartmerchantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartCartApp(sessionPreferences)
                }
            }
        }
    }
}

@Composable
fun SmartCartApp(sessionPreferences: SessionPreferences) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var hasSession by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        hasSession = sessionPreferences.authToken.first() != null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (hasSession) {
            true -> {
                MainDashboardScreen(
                    onLogout = {
                        scope.launch {
                            sessionPreferences.clearSession()
                            hasSession = false
                        }
                    }
                )
            }
            false -> {
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
                                hasSession = true
                            }
                        )
                    }

                    composable("sign_up") {
                        SignUpScreen(
                            onNavigateToSignIn = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
            null -> {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
fun MainDashboardScreen(onLogout: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "Dashboard Principal",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}