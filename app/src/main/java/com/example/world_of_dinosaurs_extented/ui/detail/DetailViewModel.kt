package com.example.world_of_dinosaurs_extented.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaurDetailUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDinosaurDetailUseCase: GetDinosaurDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val dinosaurId: String = savedStateHandle.get<String>("dinosaurId") ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDinosaur()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadDinosaur() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getDinosaurDetailUseCase(dinosaurId).collect { dino ->
                    _uiState.update { it.copy(dinosaur = dino, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavoriteUseCase(dinosaurId)
        }
    }
}
