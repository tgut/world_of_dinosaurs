package com.example.world_of_dinosaurs_extented.ui.qrscan

data class QrScanUiState(
    val scannedDinosaurId: String? = null,
    val scannedDinosaurName: String? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val hasCameraPermission: Boolean = false
)
