package com.example.world_of_dinosaurs_extented.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.model3d.Model3dConfig
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaursUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDinosaursUseCase: GetDinosaursUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val initialEra: DinosaurEra? = savedStateHandle.get<String>("era")?.let { eraName ->
        try { DinosaurEra.valueOf(eraName) } catch (_: Exception) { null }
    }

    private val _uiState = MutableStateFlow(HomeUiState(selectedEra = initialEra))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDinosaurs()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadDinosaurs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getDinosaursUseCase().collect { dinosaurs ->
                    _uiState.update { state ->
                        state.copy(
                            dinosaurs = applyFilters(dinosaurs, state),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reloadWithFilters()
    }

    fun onEraFilterChanged(era: DinosaurEra?) {
        _uiState.update { it.copy(selectedEra = era) }
        reloadWithFilters()
    }

    fun onDietFilterChanged(diet: DinosaurDiet?) {
        _uiState.update { it.copy(selectedDiet = diet) }
        reloadWithFilters()
    }

    fun onSizeFilterChanged(size: DinosaurSize?) {
        _uiState.update { it.copy(selectedSize = size) }
        reloadWithFilters()
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun toggleOnly3D() {
        _uiState.update { it.copy(only3D = !it.only3D) }
        reloadWithFilters()
    }

    fun retry() = loadDinosaurs()

    private fun reloadWithFilters() {
        viewModelScope.launch {
            try {
                getDinosaursUseCase().collect { allDinosaurs ->
                    _uiState.update { state ->
                        state.copy(dinosaurs = applyFilters(allDinosaurs, state))
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun applyFilters(
        dinosaurs: List<com.example.world_of_dinosaurs_extented.domain.model.Dinosaur>,
        state: HomeUiState
    ): List<com.example.world_of_dinosaurs_extented.domain.model.Dinosaur> {
        return dinosaurs.filter { dino ->
            val matchesSearch = state.searchQuery.isBlank() ||
                dino.name.contains(state.searchQuery, ignoreCase = true) ||
                dino.nameZh.contains(state.searchQuery) ||
                dino.scientificName.contains(state.searchQuery, ignoreCase = true)
            val matchesEra = state.selectedEra == null || dino.era == state.selectedEra
            val matchesDiet = state.selectedDiet == null || dino.diet == state.selectedDiet
            val matchesSize = state.selectedSize == null || dino.size == state.selectedSize
            val matches3D = !state.only3D || Model3dConfig.hasModel(dino.id)
            matchesSearch && matchesEra && matchesDiet && matchesSize && matches3D
        }
    }
}
