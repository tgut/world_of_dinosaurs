package com.example.world_of_dinosaurs_extented.ui.model3d

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.model3d.Model3dConfig
import com.example.world_of_dinosaurs_extented.data.model3d.ModelCacheManager
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaurDetailUseCase
import com.example.world_of_dinosaurs_extented.ui.model3d.ar.ARSceneController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ARViewUiState(
    val dinosaurName: String = "",
    val dinosaurId: String = "",
    val modelPath: String? = null,
    val isLoading: Boolean = true,
    val isDownloading: Boolean = false,
    val error: String? = null,
    val scale: Float = 1.0f,
    val isPlaced: Boolean = false
)

@HiltViewModel
class ARViewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDinosaurDetailUseCase: GetDinosaurDetailUseCase,
    val settingsManager: SettingsManager,
    private val modelCacheManager: ModelCacheManager,
    val arSceneController: ARSceneController
) : ViewModel() {

    private val dinosaurId: String = savedStateHandle.get<String>("dinosaurId") ?: ""

    private val _uiState = MutableStateFlow(ARViewUiState(dinosaurId = dinosaurId))
    val uiState: StateFlow<ARViewUiState> = _uiState.asStateFlow()

    init {
        loadModel()
    }

    private fun loadModel() {
        viewModelScope.launch {
            val language = settingsManager.languageFlow.first()
            getDinosaurDetailUseCase(dinosaurId).collect { dino ->
                val name = dino?.getLocalizedName(language) ?: ""
                _uiState.update { it.copy(dinosaurName = name) }
            }
        }

        viewModelScope.launch {
            val modelInfo = Model3dConfig.getModelInfo(dinosaurId)
            if (modelInfo == null) {
                _uiState.update { it.copy(isLoading = false, error = "No AR model available") }
                return@launch
            }

            _uiState.update { it.copy(scale = modelInfo.scale) }

            if (modelInfo.assetPath == null && modelInfo.remoteUrl != null &&
                !modelCacheManager.isModelCached(dinosaurId)) {
                _uiState.update { it.copy(isDownloading = true) }
            }

            val path = modelCacheManager.resolveModel(modelInfo)
            _uiState.update {
                it.copy(
                    modelPath = path,
                    isLoading = false,
                    isDownloading = false,
                    error = if (path == null) "Failed to load AR model" else null
                )
            }
        }
    }

    fun onModelPlaced() {
        _uiState.update { it.copy(isPlaced = true) }
    }

    fun resetPlacement() {
        _uiState.update { it.copy(isPlaced = false) }
    }
}
