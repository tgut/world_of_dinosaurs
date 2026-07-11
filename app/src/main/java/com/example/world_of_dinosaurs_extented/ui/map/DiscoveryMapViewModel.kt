package com.example.world_of_dinosaurs_extented.ui.map

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.map.MapProvider
import com.example.world_of_dinosaurs_extented.data.map.MapProviderDetector
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurMapMarker
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaurMapMarkersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMapMarkersUseCase: GetDinosaurMapMarkersUseCase,
    private val settingsManager: SettingsManager,
    private val application: Application
) : ViewModel() {

    private val focusDinosaurId: String? = savedStateHandle.get<String>("dinosaurId")?.takeIf { it.isNotBlank() }
    private val mapProviderDetector = MapProviderDetector(application)

    private val _uiState = MutableStateFlow(DiscoveryMapUiState())
    val uiState: StateFlow<DiscoveryMapUiState> = _uiState.asStateFlow()

    init {
        loadMarkers()
        observeLanguage()
        observeGlobeTimeout()
        detectMapProvider()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun observeGlobeTimeout() {
        viewModelScope.launch {
            settingsManager.globeRotateTimeoutFlow.collect { seconds ->
                _uiState.update { it.copy(globeRotateTimeoutMs = seconds * 1000L) }
            }
        }
    }

    /**
     * Detect map provider based on network connectivity
     */
    private fun detectMapProvider() {
        viewModelScope.launch {
            val savedProvider = settingsManager.getMapProvider().first()
            val provider = if (savedProvider == MapProvider.AUTO) {
                mapProviderDetector.detectBestProvider()
            } else {
                savedProvider
            }
            _uiState.update { it.copy(mapProvider = provider) }
        }
    }

    /**
     * Redetect map provider (manual trigger)
     */
    fun redetectMapProvider() {
        detectMapProvider()
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getMapMarkersUseCase().collect { markers ->
                    val focusMarker = focusDinosaurId?.let { id ->
                        markers.find { it.dinosaurId == id }
                    }
                    _uiState.update {
                        it.copy(
                            markers = markers,
                            isLoading = false,
                            selectedMarker = focusMarker,
                            focusDinosaurId = focusDinosaurId,
                            focusLat = focusMarker?.lat,
                            focusLng = focusMarker?.lng,
                            focusZoom = if (focusMarker != null) 6.0 else null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onMarkerSelected(marker: DinosaurMapMarker?) {
        _uiState.update { it.copy(selectedMarker = marker) }
    }

    fun toggleMapStyle() {
        _uiState.update {
            it.copy(
                mapStyle = when (it.mapStyle) {
                    MapStyle.FLAT -> MapStyle.SATELLITE
                    MapStyle.SATELLITE -> MapStyle.GLOBE
                    MapStyle.GLOBE -> MapStyle.FLAT
                }
            )
        }
    }

    /**
     * Set map provider manually
     */
    fun setMapProvider(provider: MapProvider) {
        viewModelScope.launch {
            settingsManager.setMapProvider(provider)
            _uiState.update { it.copy(mapProvider = provider) }
        }
    }
}