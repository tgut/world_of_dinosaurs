package com.example.world_of_dinosaurs_extented.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.ads.AdManager
import com.example.world_of_dinosaurs_extented.data.ads.AdUnitIds
import com.example.world_of_dinosaurs_extented.data.tts.TtsManager
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
    private val settingsManager: SettingsManager,
    private val ttsManager: TtsManager,
    val adManager: AdManager,
    val adUnitIds: AdUnitIds
) : ViewModel() {

    private val dinosaurId: String = savedStateHandle.get<String>("dinosaurId") ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDinosaur()
        observeLanguage()
        observeTtsSpeaking()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun observeTtsSpeaking() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
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

    fun readAloud() {
        val state = _uiState.value
        val dino = state.dinosaur ?: return
        val language = state.language

        val text = buildString {
            append(dino.getLocalizedName(language))
            append(". ")
            val desc = dino.getLocalizedDescription(language)
            if (desc.isNotBlank()) {
                append(desc)
                append(". ")
            }
            val facts = dino.getLocalizedFacts(language)
            if (facts.isNotEmpty()) {
                facts.forEach { fact ->
                    append(fact)
                    append(". ")
                }
            }
            val habitat = dino.getLocalizedHabitat(language)
            if (habitat.isNotBlank()) {
                append(habitat)
            }
        }

        viewModelScope.launch {
            val speed = settingsManager.ttsSpeedFlow.first()
            val pitch = settingsManager.ttsPitchFlow.first()
            ttsManager.speak(text, language, speed, pitch)
        }
    }

    fun stopReading() {
        ttsManager.stop()
    }

    fun toggleTranslation() {
        _uiState.update { it.copy(showTranslation = !it.showTranslation) }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
