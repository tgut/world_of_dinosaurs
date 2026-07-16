package com.example.world_of_dinosaurs_extented.ui.settings

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.map.MapProvider
import com.example.world_of_dinosaurs_extented.domain.usecase.ExportFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExportUiState(
    val isExporting: Boolean = false,
    val exportError: String? = null,
    val exportSuccessPath: String? = null,
    val exportShareIntent: Intent? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val exportFavoritesUseCase: ExportFavoritesUseCase
) : ViewModel() {

    val language: Flow<String> = settingsManager.languageFlow
    val theme: Flow<String> = settingsManager.themeFlow
    val visionApiKey: Flow<String> = settingsManager.visionApiKeyFlow
    val globeRotateTimeout: Flow<Int> = settingsManager.globeRotateTimeoutFlow
    val chatProvider: Flow<String> = settingsManager.chatProviderFlow
    val chatApiKey: Flow<String> = settingsManager.chatApiKeyFlow
    val chatBaseUrl: Flow<String> = settingsManager.chatBaseUrlFlow
    val chatModel: Flow<String> = settingsManager.chatModelFlow
    val ttsSpeed: Flow<Float> = settingsManager.ttsSpeedFlow
    val ttsPitch: Flow<Float> = settingsManager.ttsPitchFlow
    val mapProvider: Flow<String> = settingsManager.mapProviderFlow
    val visionProvider: Flow<String> = settingsManager.getVisionProvider()
    val tencentSecretId: Flow<String> = settingsManager.getTencentSecretId()
    val tencentSecretKey: Flow<String> = settingsManager.getTencentSecretKey()

    private val _exportState = MutableStateFlow(ExportUiState())
    val exportState: StateFlow<ExportUiState> = _exportState.asStateFlow()

    fun setGlobeRotateTimeout(seconds: Int) {
        viewModelScope.launch { settingsManager.setGlobeRotateTimeout(seconds) }
    }

    fun setVisionApiKey(key: String) {
        viewModelScope.launch { settingsManager.setVisionApiKey(key.trim()) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(lang)
            val locale = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }

    fun setTheme(themeValue: String) {
        viewModelScope.launch { settingsManager.setTheme(themeValue) }
    }

    fun setChatProvider(provider: String) {
        viewModelScope.launch { settingsManager.setChatProvider(provider) }
    }

    fun setChatApiKey(key: String) {
        viewModelScope.launch { settingsManager.setChatApiKey(key.trim()) }
    }

    fun setChatBaseUrl(url: String) {
        viewModelScope.launch { settingsManager.setChatBaseUrl(url.trim()) }
    }

    fun setChatModel(model: String) {
        viewModelScope.launch { settingsManager.setChatModel(model.trim()) }
    }

    fun setTtsSpeed(speed: Float) {
        viewModelScope.launch { settingsManager.setTtsSpeed(speed) }
    }

    fun setTtsPitch(pitch: Float) {
        viewModelScope.launch { settingsManager.setTtsPitch(pitch) }
    }

    fun setMapProvider(provider: String) {
        viewModelScope.launch {
            settingsManager.setMapProvider(MapProvider.fromKey(provider))
        }
    }

    fun setVisionProvider(provider: String) {
        viewModelScope.launch { settingsManager.setVisionProvider(provider) }
    }

    fun setTencentSecretId(secretId: String) {
        viewModelScope.launch { settingsManager.setTencentSecretId(secretId.trim()) }
    }

    fun setTencentSecretKey(secretKey: String) {
        viewModelScope.launch { settingsManager.setTencentSecretKey(secretKey.trim()) }
    }

    fun exportFavorites() {
        viewModelScope.launch {
            _exportState.value = ExportUiState(isExporting = true)
            try {
                val result = exportFavoritesUseCase()
                _exportState.value = ExportUiState(
                    isExporting = false,
                    exportSuccessPath = result.filePath,
                    exportShareIntent = result.shareIntent
                )
            } catch (e: Exception) {
                _exportState.value = ExportUiState(
                    isExporting = false,
                    exportError = e.message ?: "Export failed"
                )
            }
        }
    }

    fun dismissExportResult() {
        _exportState.value = ExportUiState()
    }
}