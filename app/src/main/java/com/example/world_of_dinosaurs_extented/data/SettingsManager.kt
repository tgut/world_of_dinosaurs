package com.example.world_of_dinosaurs_extented.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dino_settings")

enum class ChatProvider(val key: String, val displayName: String, val baseUrl: String, val defaultModel: String) {
    DEEPSEEK("deepseek", "DeepSeek", "https://api.deepseek.com/", "deepseek-chat"),
    QWEN("qwen", "Qwen", "https://dashscope.aliyuncs.com/compatible-mode/v1/", "qwen-turbo"),
    GEMINI("gemini", "Google Gemini", "https://generativelanguage.googleapis.com/v1beta/openai/", "gemini-2.5-flash-lite"),
    CUSTOM("custom", "Custom", "", "");

    companion object {
        fun fromKey(key: String): ChatProvider = entries.find { it.key == key } ?: DEEPSEEK
    }
}

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

    val chatProviderFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHAT_PROVIDER_KEY] ?: ChatProvider.DEEPSEEK.key
    }

    val chatApiKeyFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHAT_API_KEY] ?: ""
    }

    val chatBaseUrlFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHAT_BASE_URL_KEY] ?: ""
    }

    val chatModelFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHAT_MODEL_KEY] ?: ""
    }

    val ttsSpeedFlow: Flow<Float> = dataStore.data.map { prefs ->
        prefs[TTS_SPEED_KEY] ?: 1.0f
    }

    val ttsPitchFlow: Flow<Float> = dataStore.data.map { prefs ->
        prefs[TTS_PITCH_KEY] ?: 1.0f
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

    suspend fun setChatProvider(provider: String) {
        dataStore.edit { prefs -> prefs[CHAT_PROVIDER_KEY] = provider }
    }

    suspend fun setChatApiKey(key: String) {
        dataStore.edit { prefs -> prefs[CHAT_API_KEY] = key }
    }

    suspend fun setChatBaseUrl(url: String) {
        dataStore.edit { prefs -> prefs[CHAT_BASE_URL_KEY] = url }
    }

    suspend fun setChatModel(model: String) {
        dataStore.edit { prefs -> prefs[CHAT_MODEL_KEY] = model }
    }

    suspend fun setTtsSpeed(speed: Float) {
        dataStore.edit { prefs -> prefs[TTS_SPEED_KEY] = speed }
    }

    suspend fun setTtsPitch(pitch: Float) {
        dataStore.edit { prefs -> prefs[TTS_PITCH_KEY] = pitch }
    }

    fun getResolvedChatBaseUrl(provider: ChatProvider, customUrl: String): String {
        return if (provider == ChatProvider.CUSTOM) customUrl else provider.baseUrl
    }

    fun getResolvedChatModel(provider: ChatProvider, customModel: String): String {
        return if (provider == ChatProvider.CUSTOM) customModel else provider.defaultModel
    }

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val VISION_API_KEY = stringPreferencesKey("vision_api_key")
        private val GLOBE_ROTATE_TIMEOUT = stringPreferencesKey("globe_rotate_timeout")
        private val CHAT_PROVIDER_KEY = stringPreferencesKey("chat_provider")
        private val CHAT_API_KEY = stringPreferencesKey("chat_api_key")
        private val CHAT_BASE_URL_KEY = stringPreferencesKey("chat_base_url")
        private val CHAT_MODEL_KEY = stringPreferencesKey("chat_model")
        private val TTS_SPEED_KEY = floatPreferencesKey("tts_speed")
        private val TTS_PITCH_KEY = floatPreferencesKey("tts_pitch")
    }
}
