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
}
