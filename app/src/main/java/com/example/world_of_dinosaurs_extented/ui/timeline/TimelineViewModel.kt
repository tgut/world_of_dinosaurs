package com.example.world_of_dinosaurs_extented.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.usecase.GetDinosaursUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getDinosaursUseCase: GetDinosaursUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadTimeline()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadTimeline() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getDinosaursUseCase().collect { dinosaurs ->
                    val grouped = DinosaurEra.entries.associateWith { era ->
                        dinosaurs.filter { it.era == era }
                    }
                    _uiState.update { it.copy(eraGroups = grouped, isLoading = false) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
