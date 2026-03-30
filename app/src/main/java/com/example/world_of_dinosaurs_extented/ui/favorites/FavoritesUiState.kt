package com.example.world_of_dinosaurs_extented.ui.favorites

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur

data class FavoritesUiState(
    val favorites: List<Dinosaur> = emptyList(),
    val isLoading: Boolean = true,
    val language: String = "en"
)
