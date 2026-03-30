package com.example.world_of_dinosaurs_extented.ui.recognition

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.domain.model.RecognitionMatch
import com.example.world_of_dinosaurs_extented.domain.usecase.RecognizeDinosaurUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DinoRecognitionUiState(
    val isAnalyzing: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val matches: List<RecognitionMatch> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class DinoRecognitionViewModel @Inject constructor(
    private val recognizeDinosaurUseCase: RecognizeDinosaurUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DinoRecognitionUiState())
    val uiState: StateFlow<DinoRecognitionUiState> = _uiState.asStateFlow()

    fun onImageCaptured(base64Image: String, bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                isAnalyzing = true,
                capturedBitmap = bitmap,
                matches = emptyList(),
                error = null,
                hasSearched = false
            )
        }

        viewModelScope.launch {
            try {
                val matches = recognizeDinosaurUseCase(base64Image)
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        matches = matches,
                        hasSearched = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        error = e.message ?: "Recognition failed",
                        hasSearched = true
                    )
                }
            }
        }
    }
}
