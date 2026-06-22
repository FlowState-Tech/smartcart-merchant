package com.smartcart_merchant.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.auth.domain.usecase.SignInUseCase
import com.smartcart_merchant.features.auth.presentation.state.SignInUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sessionPreferences.rememberMe,
                sessionPreferences.savedUsername
            ) { rememberMe, savedUsername ->
                rememberMe to savedUsername
            }.collect { (rememberMe, savedUsername) ->
                _uiState.update { state ->
                    state.copy(
                        rememberMe = rememberMe,
                        username = if (rememberMe && !savedUsername.isNullOrBlank()) {
                            savedUsername
                        } else {
                            state.username
                        }
                    )
                }
            }
        }
    }

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRememberMeChanged(rememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = rememberMe) }
    }

    fun onPasswordVisibilityChanged() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login() {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos.") }
            return
        }

        viewModelScope.launch {
            signInUseCase(
                currentState.username,
                currentState.password,
                currentState.rememberMe
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                }
            }
        }
    }
}
