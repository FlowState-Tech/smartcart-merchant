package com.smartcart_merchant.features.store.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.smartcart_merchant.features.store.presentation.viewmodel.StoreViewModel

@Composable
fun StoreScreen(
    onStoreSuccess: () -> Unit,
    googleMapsApiKey: String,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hoursConfig by viewModel.operatingHoursConfig.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val peruLocation = LatLng(-12.0464, -76.9083)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(peruLocation, 14f)
    }

    var mapProperties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = false))
    }

    LaunchedEffect(googleMapsApiKey) {
        mapProperties = MapProperties(isMyLocationEnabled = false)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.onStoreCreated()
            onStoreSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4EA8DE),
                            Color(0xFF56CFE1),
                            Color(0xFF70E000)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Registrar Sucursal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )

                        Text(
                            text = "Completa los datos de tu sucursal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                            value = uiState.ruc,
                            onValueChange = {},
                            label = { Text("RUC") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color(0xFF1A1A2E),
                                disabledBorderColor = Color(0xFF56CFE1),
                                disabledLabelColor = Color(0xFF56CFE1)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.storeName,
                            onValueChange = { viewModel.setStoreName(it) },
                            label = { Text("Nombre de la Sucursal") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF56CFE1)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.street,
                                onValueChange = { viewModel.setStreet(it) },
                                label = { Text("Dirección / Calle") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )

                            OutlinedTextField(
                                value = uiState.district,
                                onValueChange = { viewModel.setDistrict(it) },
                                label = { Text("Distrito") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Coordenadas de Ubicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.latitude?.toString() ?: "",
                                onValueChange = {
                                    when {
                                        it.isBlank() -> viewModel.setLocation(null, uiState.longitude)
                                        else -> it.toDoubleOrNull()?.let { lat ->
                                            viewModel.setLocation(lat, uiState.longitude)
                                        }
                                    }
                                },
                                label = { Text("Latitud") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )
                            OutlinedTextField(
                                value = uiState.longitude?.toString() ?: "",
                                onValueChange = {
                                    when {
                                        it.isBlank() -> viewModel.setLocation(uiState.latitude, null)
                                        else -> it.toDoubleOrNull()?.let { lng ->
                                            viewModel.setLocation(uiState.latitude, lng)
                                        }
                                    }
                                },
                                label = { Text("Longitud") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Horarios de Atención",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Selecciona los días de atención",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val days = listOf(
                            "Monday" to "Lun",
                            "Tuesday" to "Mar",
                            "Wednesday" to "Mié",
                            "Thursday" to "Jue",
                            "Friday" to "Vie",
                            "Saturday" to "Sáb",
                            "Sunday" to "Dom"
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            days.forEach { (fullDay, shortDay) ->
                                FilterChip(
                                    selected = hoursConfig.selectedDays.contains(fullDay),
                                    onClick = { viewModel.toggleDay(fullDay) },
                                    label = { Text(shortDay) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF56CFE1),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Horario para todos los días seleccionados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = hoursConfig.openTime,
                                onValueChange = { viewModel.setOpenTime(it) },
                                label = { Text("Apertura") },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("09:00") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )

                            Text(
                                text = "-",
                                color = Color(0xFF888888),
                                style = MaterialTheme.typography.titleLarge
                            )

                            OutlinedTextField(
                                value = hoursConfig.closeTime,
                                onValueChange = { viewModel.setCloseTime(it) },
                                label = { Text("Cierre") },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("18:00") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF56CFE1)
                                )
                            )
                        }

                        if (hoursConfig.selectedDays.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val selectedShortDays = hoursConfig.selectedDays.mapNotNull { day ->
                                days.find { it.first == day }?.second
                            }
                            Text(
                                text = "Días: ${selectedShortDays.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF56CFE1),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Horario: ${hoursConfig.openTime} - ${hoursConfig.closeTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF888888)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.createStore() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF70E000)
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Registrar Sucursal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ubicación en el Mapa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Toca el mapa para seleccionar la ubicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F4F8)
                            )
                        ) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = mapProperties,
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = true,
                                    myLocationButtonEnabled = false,
                                    mapToolbarEnabled = false
                                ),
                                onMapClick = { latLng ->
                                    viewModel.setLocation(latLng.latitude, latLng.longitude)
                                }
                            ) {
                                if (uiState.latitude != null && uiState.longitude != null) {
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(
                                                uiState.latitude!!,
                                                uiState.longitude!!
                                            )
                                        ),
                                        title = uiState.storeName.ifEmpty { "Sucursal" }
                                    )
                                }
                            }
                        }

                        if (uiState.latitude != null && uiState.longitude != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lat: ${String.format("%.6f", uiState.latitude)} | Lng: ${String.format("%.6f", uiState.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF56CFE1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}