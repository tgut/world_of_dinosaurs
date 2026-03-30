package com.example.world_of_dinosaurs_extented.ui.model3d

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaurDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Model3DUiState(
    val dinosaurName: String = "",
    val modelPath: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class Model3DViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDinosaurDetailUseCase: GetDinosaurDetailUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val dinosaurId: String = savedStateHandle.get<String>("dinosaurId") ?: ""

    private val _uiState = MutableStateFlow(Model3DUiState())
    val uiState: StateFlow<Model3DUiState> = _uiState.asStateFlow()

    init {
        loadModel()
    }

    private fun loadModel() {
        viewModelScope.launch {
            val language = settingsManager.languageFlow.first()
            getDinosaurDetailUseCase(dinosaurId).collect { dino ->
                _uiState.update {
                    it.copy(
                        dinosaurName = dino?.getLocalizedName(language) ?: "",
                        modelPath = dino?.model3dUrl,
                        isLoading = false
                    )
                }
            }
        }
    }
}
