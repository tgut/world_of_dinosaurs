package com.example.world_of_dinosaurs_extented.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dino_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val languageFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "en"
    }

    val themeFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "system"
    }

    val visionApiKeyFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[VISION_API_KEY] ?: ""
    }

    val globeRotateTimeoutFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[GLOBE_ROTATE_TIMEOUT]?.toIntOrNull() ?: 10
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { prefs -> prefs[THEME_KEY] = theme }
    }

    suspend fun setVisionApiKey(key: String) {
        dataStore.edit { prefs -> prefs[VISION_API_KEY] = key }
    }

    suspend fun getVisionApiKey(): String {
        return dataStore.data.map { prefs -> prefs[VISION_API_KEY] ?: "" }.first()
    }

    suspend fun setGlobeRotateTimeout(seconds: Int) {
        dataStore.edit { prefs -> prefs[GLOBE_ROTATE_TIMEOUT] = seconds.toString() }
    }

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val VISION_API_KEY = stringPreferencesKey("vision_api_key")
        private val GLOBE_ROTATE_TIMEOUT = stringPreferencesKey("globe_rotate_timeout")
    }
}
