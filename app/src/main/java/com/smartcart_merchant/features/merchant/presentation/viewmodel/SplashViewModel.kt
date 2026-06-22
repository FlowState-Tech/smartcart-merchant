package com.smartcart_merchant.features.merchant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.merchant.domain.usecase.GetMerchantProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object Loading : SplashDestination()
    object Auth : SplashDestination()
    object Verification : SplashDestination()
    object StoreSetup : SplashDestination()
    object Dashboard : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getMerchantProfileUseCase: GetMerchantProfileUseCase,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        loadDestination()
    }

    fun loadDestination() {
        viewModelScope.launch {
            _destination.value = SplashDestination.Loading

            val token = sessionPreferences.authToken.first()
            if (token.isNullOrBlank()) {
                _destination.value = SplashDestination.Auth
                return@launch
            }

            when (val result = getMerchantProfileUseCase()) {
                is Resource.Success -> {
                    val profile = result.data
                    if (profile == null) {
                        clearSessionAndGoToAuth()
                        return@launch
                    }

                    if (profile.isVerified) {
                        profile.applicationId?.let { applicationId ->
                            profile.companyName?.let { companyName ->
                                profile.ruc?.let { ruc ->
                                    sessionPreferences.saveVerificationStatus(
                                        applicationId = applicationId.toString(),
                                        companyName = companyName,
                                        ruc = ruc
                                    )
                                }
                            }
                        }
                    }

                    val firstStore = profile.stores.firstOrNull()
                    if (profile.isVerified && firstStore != null) {
                        sessionPreferences.saveStoreId(firstStore.storeId.toString())
                        _destination.value = SplashDestination.Dashboard
                    } else if (profile.isVerified) {
                        _destination.value = SplashDestination.StoreSetup
                    } else {
                        _destination.value = SplashDestination.Verification
                    }
                }
                is Resource.Error -> {
                    clearSessionAndGoToAuth()
                }
                is Resource.Loading -> {
                    _destination.value = SplashDestination.Loading
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            clearSessionAndGoToAuth()
        }
    }

    private suspend fun clearSessionAndGoToAuth() {
        sessionPreferences.clearSession()
        _destination.value = SplashDestination.Auth
    }
}
