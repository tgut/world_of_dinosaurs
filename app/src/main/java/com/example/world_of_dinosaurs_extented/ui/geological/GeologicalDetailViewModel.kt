package com.example.world_of_dinosaurs_extented.ui.geological

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.tts.TtsManager
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.GeologicalPeriod
import com.example.world_of_dinosaurs_extented.domain.repository.GeologicalPeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeologicalDetailUiState(
    val period: GeologicalPeriod? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val language: String = "en",
    val isSpeaking: Boolean = false
)

@HiltViewModel
class GeologicalDetailViewModel @Inject constructor(
    private val geologicalPeriodRepository: GeologicalPeriodRepository,
    private val ttsManager: TtsManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeologicalDetailUiState())
    val uiState: StateFlow<GeologicalDetailUiState> = _uiState.asStateFlow()

    init {
        observeTtsSpeaking()
    }

    private fun observeTtsSpeaking() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                _uiState.value = _uiState.value.copy(isSpeaking = speaking)
            }
        }
    }

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

    fun readAloud() {
        val state = _uiState.value
        val period = state.period ?: return
        val lang = state.language

        viewModelScope.launch {
            val text = buildString {
                append(period.getLocalizedName(lang))
                append(". ")
                if (lang == "zh") {
                    append("距今${period.startMya}到${period.endMya}百万年。")
                    append("气候：${period.getLocalizedClimate(lang)}。")
                    append("平均温度${period.averageTempC}度。")
                    append("主要植物：${period.getLocalizedFloraDominant(lang).joinToString("、")}。")
                    append("同期生物：${period.getLocalizedFaunaContemporary(lang).joinToString("、")}。")
                    val events = period.getLocalizedMajorEvents(lang)
                    if (events.isNotEmpty()) {
                        append("主要事件：${events.joinToString("。")}。")
                    }
                    period.getLocalizedExtinctionEvent(lang)?.let {
                        append("灭绝事件：$it")
                    }
                } else {
                    append("${period.startMya} to ${period.endMya} million years ago. ")
                    append("Climate: ${period.getLocalizedClimate(lang)}. ")
                    append("Average temperature: ${period.averageTempC} degrees Celsius. ")
                    append("Dominant flora: ${period.getLocalizedFloraDominant(lang).joinToString(", ")}. ")
                    append("Contemporary fauna: ${period.getLocalizedFaunaContemporary(lang).joinToString(", ")}. ")
                    val events = period.getLocalizedMajorEvents(lang)
                    if (events.isNotEmpty()) {
                        append("Major events: ${events.joinToString(". ")}. ")
                    }
                    period.getLocalizedExtinctionEvent(lang)?.let {
                        append("Extinction event: $it")
                    }
                }
            }
            val speed = settingsManager.ttsSpeedFlow.first()
            val pitch = settingsManager.ttsPitchFlow.first()
            ttsManager.speak(text, lang, speed, pitch)
        }
    }

    fun stopReading() {
        ttsManager.stop()
    }

    fun setLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
