package com.example.world_of_dinosaurs_extented.ui.qrscan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScanViewModel @Inject constructor(
    private val dinosaurRepository: DinosaurRepository,
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScanUiState())
    val uiState: StateFlow<QrScanUiState> = _uiState.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = granted) }
    }

    fun onQrCodeScanned(content: String) {
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(isProcessing = true, errorMessage = null) }

        viewModelScope.launch {
            val dinosaurId = content.trim()
            try {
                dinosaurRepository.getDinosaurs().first().find { it.id == dinosaurId }?.let { dino ->
                    scanHistoryRepository.recordScan(dinosaurId)
                    _uiState.update {
                        it.copy(
                            scannedDinosaurId = dinosaurId,
                            scannedDinosaurName = dino.name,
                            isProcessing = false
                        )
                    }
                } ?: run {
                    _uiState.update {
                        it.copy(isProcessing = false, errorMessage = "Dinosaur not found: $dinosaurId")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false, errorMessage = e.message) }
            }
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(isProcessing = true, errorMessage = null) }

        try {
            val image = InputImage.fromFilePath(context, uri)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val qrValue = barcodes.firstOrNull()?.rawValue
                    if (qrValue != null) {
                        onQrCodeScanned(qrValue)
                    } else {
                        _uiState.update {
                            it.copy(isProcessing = false, errorMessage = "No QR code found in this image")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(isProcessing = false, errorMessage = e.message ?: "Failed to scan image")
                    }
                }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isProcessing = false, errorMessage = e.message ?: "Failed to load image")
            }
        }
    }

    fun resetScanResult() {
        _uiState.update { it.copy(scannedDinosaurId = null, scannedDinosaurName = null, errorMessage = null) }
    }
}
