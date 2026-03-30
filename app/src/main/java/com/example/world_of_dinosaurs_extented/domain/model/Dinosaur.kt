package com.example.world_of_dinosaurs_extented.domain.model

data class Dinosaur(
    val id: String,
    val name: String,
    val nameZh: String,
    val scientificName: String,
    val description: String,
    val descriptionZh: String,
    val era: DinosaurEra,
    val periodYearsAgo: String,
    val diet: DinosaurDiet,
    val size: DinosaurSize,
    val lengthMeters: Double?,
    val weightKg: Double?,
    val heightMeters: Double?,
    val imageUrl: String?,
    val facts: List<String>,
    val factsZh: List<String>,
    val habitat: String,
    val habitatZh: String,
    val discoveryYear: Int?,
    val discoveryLocation: String,
    val model3dUrl: String?,
    val isFavorite: Boolean = false,
    val isFeatured: Boolean = false
) {
    fun getLocalizedName(language: String): String =
        if (language == "zh") nameZh else name

    fun getLocalizedDescription(language: String): String =
        if (language == "zh") descriptionZh else description

    fun getLocalizedFacts(language: String): List<String> =
        if (language == "zh") factsZh else facts

    fun getLocalizedHabitat(language: String): String =
        if (language == "zh") habitatZh else habitat
}
