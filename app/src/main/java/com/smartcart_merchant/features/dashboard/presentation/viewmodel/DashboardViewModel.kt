package com.smartcart_merchant.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.smartcart_merchant.features.dashboard.domain.model.DashboardDestination
import com.smartcart_merchant.features.dashboard.presentation.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun onDestinationSelected(destination: DashboardDestination) {
        _uiState.update { it.copy(selectedDestination = destination) }
    }
}
