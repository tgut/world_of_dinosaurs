package com.example.world_of_dinosaurs_extented.ui.map

import com.example.world_of_dinosaurs_extented.domain.model.DinosaurMapMarker

enum class MapStyle { FLAT, SATELLITE, GLOBE }

data class DiscoveryMapUiState(
    val markers: List<DinosaurMapMarker> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedMarker: DinosaurMapMarker? = null,
    val mapStyle: MapStyle = MapStyle.FLAT,
    val focusDinosaurId: String? = null,
    val focusLat: Double? = null,
    val focusLng: Double? = null,
    val focusZoom: Double? = null,
    val language: String = "en",
    val globeRotateTimeoutMs: Long = 10000L
)
