package com.example.world_of_dinosaurs_extented.ui.home

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize

data class HomeUiState(
    val dinosaurs: List<Dinosaur> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedEra: DinosaurEra? = null,
    val selectedDiet: DinosaurDiet? = null,
    val selectedSize: DinosaurSize? = null,
    val isGridView: Boolean = true,
    val only3D: Boolean = false,
    val language: String = "en"
)
