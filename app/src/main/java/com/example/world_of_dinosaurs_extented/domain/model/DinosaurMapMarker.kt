package com.example.world_of_dinosaurs_extented.domain.model

data class DinosaurMapMarker(
    val dinosaurId: String,
    val name: String,
    val nameZh: String,
    val era: DinosaurEra,
    val discoveryLocation: String,
    val lat: Double,
    val lng: Double
) {
    fun getLocalizedName(language: String): String =
        if (language == "zh") nameZh else name
}
