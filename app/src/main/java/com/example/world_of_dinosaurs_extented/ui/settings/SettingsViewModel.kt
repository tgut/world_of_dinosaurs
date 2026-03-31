package com.example.world_of_dinosaurs_extented.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
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

    fun setGlobeRotateTimeout(seconds: Int) {
        viewModelScope.launch {
            settingsManager.setGlobeRotateTimeout(seconds)
        }
    }

    fun setVisionApiKey(key: String) {
        viewModelScope.launch {
            settingsManager.setVisionApiKey(key.trim())
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(lang)
            val locale = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }

    fun setTheme(themeValue: String) {
        viewModelScope.launch {
            settingsManager.setTheme(themeValue)
        }
    }

    fun setChatProvider(provider: String) {
        viewModelScope.launch {
            settingsManager.setChatProvider(provider)
        }
    }

    fun setChatApiKey(key: String) {
        viewModelScope.launch {
            settingsManager.setChatApiKey(key.trim())
        }
    }

    fun setChatBaseUrl(url: String) {
        viewModelScope.launch {
            settingsManager.setChatBaseUrl(url.trim())
        }
    }

    fun setChatModel(model: String) {
        viewModelScope.launch {
            settingsManager.setChatModel(model.trim())
        }
    }

    fun setTtsSpeed(speed: Float) {
        viewModelScope.launch {
            settingsManager.setTtsSpeed(speed)
        }
    }

    fun setTtsPitch(pitch: Float) {
        viewModelScope.launch {
            settingsManager.setTtsPitch(pitch)
        }
    }
}
