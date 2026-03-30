package com.example.world_of_dinosaurs_extented.ui.scanhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanHistoryViewModel @Inject constructor(
    private val scanHistoryRepository: ScanHistoryRepository,
    private val dinosaurRepository: DinosaurRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanHistoryUiState())
    val uiState: StateFlow<ScanHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                scanHistoryRepository.getAllScans(),
                dinosaurRepository.getDinosaurs()
            ) { scans, dinosaurs ->
                val dinoMap = dinosaurs.associateBy { it.id }
                val items = scans.mapNotNull { scan ->
                    dinoMap[scan.dinosaurId]?.let { dino ->
                        ScanHistoryItem(dinosaur = dino, scannedAt = scan.scannedAt)
                    }
                }
                val distinctCount = items.map { it.dinosaur.id }.distinct().size
                ScanHistoryUiState(
                    items = items,
                    isLoading = false,
                    canStartReviewQuiz = distinctCount >= 3
                )
            }.collect { state ->
                _uiState.update {
                    it.copy(
                        items = state.items,
                        isLoading = state.isLoading,
                        canStartReviewQuiz = state.canStartReviewQuiz
                    )
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            scanHistoryRepository.clearAll()
        }
    }
}
