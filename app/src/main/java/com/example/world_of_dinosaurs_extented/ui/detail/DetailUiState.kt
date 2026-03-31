package com.example.world_of_dinosaurs_extented.ui.detail

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur

data class DetailUiState(
    val dinosaur: Dinosaur? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val language: String = "en",
    val isSpeaking: Boolean = false,
    val showTranslation: Boolean = false
)
