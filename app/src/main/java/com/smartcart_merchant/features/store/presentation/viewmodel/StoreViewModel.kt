package com.smartcart_merchant.features.store.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.store.domain.model.Address
import com.smartcart_merchant.features.store.domain.model.OperatingHour
import com.smartcart_merchant.features.store.domain.model.Store
import com.smartcart_merchant.features.store.domain.usecase.CreateStoreUseCase
import com.smartcart_merchant.features.store.domain.usecase.ReverseGeocodeUseCase
import com.smartcart_merchant.features.store.presentation.state.StoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OperatingHoursConfig(
    val selectedDays: Set<String> = emptySet(),
    val openTime: String = "09:00",
    val closeTime: String = "18:00"
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val createStoreUseCase: CreateStoreUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _operatingHoursConfig = MutableStateFlow(OperatingHoursConfig())
    val operatingHoursConfig: StateFlow<OperatingHoursConfig> = _operatingHoursConfig.asStateFlow()

    init {
        loadMerchantData()
    }

    private fun loadMerchantData() {
        viewModelScope.launch {
            val merchantId = sessionPreferences.merchantId.first()
            val ruc = sessionPreferences.ruc.first()
            _uiState.update {
                it.copy(
                    merchantId = merchantId ?: "",
                    ruc = ruc ?: ""
                )
            }
        }
    }

    fun setStoreName(name: String) {
        _uiState.update { it.copy(storeName = name) }
    }

    fun setStreet(street: String) {
        _uiState.update { it.copy(street = street) }
    }

    fun setDistrict(district: String) {
        _uiState.update { it.copy(district = district) }
    }

    fun setLocation(latitude: Double?, longitude: Double?) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude) }

        if (latitude != null && longitude != null) {
            viewModelScope.launch {
                when (val result = reverseGeocodeUseCase(latitude, longitude)) {
                    is Resource.Success -> {
                        result.data?.let { geocoded ->
                            _uiState.update { state ->
                                state.copy(
                                    street = state.street.ifBlank { geocoded.street },
                                    district = state.district.ifBlank { geocoded.district }
                                )
                            }
                        }
                    }
                    is Resource.Error -> { /* Silencioso: el usuario puede completar manualmente */ }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun toggleDay(day: String) {
        _operatingHoursConfig.update { config ->
            val newDays = if (config.selectedDays.contains(day)) {
                config.selectedDays - day
            } else {
                config.selectedDays + day
            }
            config.copy(selectedDays = newDays)
        }
    }

    fun setOpenTime(time: String) {
        _operatingHoursConfig.update { it.copy(openTime = time) }
    }

    fun setCloseTime(time: String) {
        _operatingHoursConfig.update { it.copy(closeTime = time) }
    }

    fun createStore() {
        val state = _uiState.value
        val hoursConfig = _operatingHoursConfig.value

        if (state.merchantId.isBlank()) {
            _uiState.update { it.copy(error = "Merchant ID no encontrado. Inicie sesión nuevamente.") }
            return
        }

        if (state.storeName.isBlank() ||
            state.street.isBlank() || state.district.isBlank() ||
            state.latitude == null || state.longitude == null) {
            _uiState.update { it.copy(error = "Por favor complete todos los campos") }
            return
        }

        if (hoursConfig.selectedDays.isEmpty()) {
            _uiState.update { it.copy(error = "Seleccione al menos un día") }
            return
        }

        val openTime = normalizeTime(hoursConfig.openTime)
        val closeTime = normalizeTime(hoursConfig.closeTime)

        if (openTime == null || closeTime == null) {
            _uiState.update { it.copy(error = "Formato de hora inválido. Use HH:MM o HH:MM:SS") }
            return
        }

        val operatingHours = hoursConfig.selectedDays.map { day ->
            OperatingHour(day, openTime, closeTime)
        }

        val store = Store(
            merchantId = state.merchantId,
            name = state.storeName,
            ruc = state.ruc,
            address = Address(
                street = state.street,
                district = state.district,
                latitude = state.latitude,
                longitude = state.longitude
            ),
            operatingHours = operatingHours
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = createStoreUseCase(store)) {
                is Resource.Success -> {
                    result.data?.id?.let { storeId ->
                        sessionPreferences.saveStoreId(storeId)
                    }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    val message = when {
                        result.message?.contains("Duplicate branch coordinates", ignoreCase = true) == true ->
                            "Ya existe una sucursal registrada en estas coordenadas (US10 E3)."
                        result.message?.contains("RUC already exists", ignoreCase = true) == true ->
                            "El RUC ya está registrado en el sistema."
                        else -> result.message
                    }
                    _uiState.update { it.copy(isLoading = false, error = message) }
                }
                is Resource.Loading -> { }
            }
        }
    }

    private fun normalizeTime(time: String): String? {
        val trimmed = time.trim()
        return when (trimmed.length) {
            5 -> "$trimmed:00" // HH:MM -> HH:MM:SS
            8 -> trimmed // HH:MM:SS
            else -> null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onStoreCreated() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}