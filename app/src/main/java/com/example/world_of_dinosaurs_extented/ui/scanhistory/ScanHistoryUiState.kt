package com.example.world_of_dinosaurs_extented.ui.scanhistory

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur

data class ScanHistoryItem(
    val dinosaur: Dinosaur,
    val scannedAt: Long
)

data class ScanHistoryUiState(
    val items: List<ScanHistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val language: String = "en",
    val canStartReviewQuiz: Boolean = false
)
