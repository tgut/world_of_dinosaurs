package com.example.world_of_dinosaurs_extented.ui.timeline

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra

data class TimelineUiState(
    val eraGroups: Map<DinosaurEra, List<Dinosaur>> = emptyMap(),
    val isLoading: Boolean = true,
    val language: String = "en"
)
