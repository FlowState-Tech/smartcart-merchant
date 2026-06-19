package com.smartcart_merchant.features.verification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.verification.domain.model.VerificationStatus
import com.smartcart_merchant.features.verification.domain.usecase.VerifyRucUseCase
import com.smartcart_merchant.features.verification.presentation.state.VerificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val verifyRucUseCase: VerifyRucUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    fun onRucChanged(ruc: String) {
        _uiState.update { it.copy(ruc = ruc, errorMessage = null) }
    }

    fun verify() {
        val currentState = _uiState.value
        if (currentState.ruc.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a RUC") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = verifyRucUseCase(currentState.ruc)

            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                is Resource.Success -> {
                    val data = result.data!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            status = data.status,
                            companyName = data.companyName,
                            isVerified = data.status == VerificationStatus.VERIFIED,
                            errorMessage = if (data.status == VerificationStatus.REJECTED) {
                                "Verification rejected. Please check your RUC and try again."
                            } else null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isVerified = false
                        )
                    }
                }
            }
        }
    }

    fun retryVerification() {
        _uiState.update {
            it.copy(
                ruc = "",
                status = null,
                companyName = null,
                isVerified = false,
                errorMessage = null
            )
        }
    }
}
