package com.example.world_of_dinosaurs_extented.ui.geological

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.GeologicalPeriod
import com.example.world_of_dinosaurs_extented.domain.repository.GeologicalPeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeologicalDetailUiState(
    val period: GeologicalPeriod? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val language: String = "en"
)

@HiltViewModel
class GeologicalDetailViewModel @Inject constructor(
    private val geologicalPeriodRepository: GeologicalPeriodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeologicalDetailUiState())
    val uiState: StateFlow<GeologicalDetailUiState> = _uiState.asStateFlow()

    fun loadPeriod(era: DinosaurEra, language: String = "en") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, language = language)
            try {
                val period = geologicalPeriodRepository.getPeriodByEra(era)
                if (period != null) {
                    _uiState.value = _uiState.value.copy(
                        period = period,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Period not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun setLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }
}
