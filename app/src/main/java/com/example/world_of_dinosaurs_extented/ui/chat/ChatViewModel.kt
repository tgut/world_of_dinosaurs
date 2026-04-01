package com.example.world_of_dinosaurs_extented.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatMessageDto
import com.example.world_of_dinosaurs_extented.data.tts.TtsManager
import com.example.world_of_dinosaurs_extented.domain.repository.ChatRepository
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val dinosaurRepository: DinosaurRepository,
    private val settingsManager: SettingsManager,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val dinosaurId: String? = savedStateHandle.get<String>("dinosaurId")?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        observeLanguage()
        observeTtsSpeaking()
        if (dinosaurId != null) {
            loadDinosaurContext()
        }
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadDinosaurContext() {
        viewModelScope.launch {
            try {
                val language = settingsManager.languageFlow.first()
                val dino = dinosaurRepository.getDinosaurById(dinosaurId!!).first()
                if (dino != null) {
                    val name = dino.getLocalizedName(language)
                    val context = buildString {
                        append("The user is currently viewing $name (${dino.scientificName}). ")
                        append("Era: ${dino.era.name}. Diet: ${dino.diet.name}. ")
                        dino.lengthMeters?.let { append("Length: ${it}m. ") }
                        dino.weightKg?.let { append("Weight: ${it}kg. ") }
                        dino.discoveryYear?.let { append("Discovered: $it. ") }
                        if (dino.discoveryLocation.isNotBlank()) {
                            append("Location: ${dino.discoveryLocation}. ")
                        }
                    }
                    _uiState.update {
                        it.copy(dinosaurContext = context, dinosaurName = name)
                    }
                }
            } catch (_: Exception) { /* ignore */ }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(role = "user", content = text)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val language = _uiState.value.language
                val systemPrompt = chatRepository.buildSystemPrompt(language, _uiState.value.dinosaurContext)

                val apiMessages = mutableListOf(
                    ChatMessageDto(role = "system", content = systemPrompt)
                )
                // Include recent conversation history (last 10 messages to avoid token limit)
                val recentMessages = _uiState.value.messages.takeLast(10)
                recentMessages.forEach { msg ->
                    apiMessages.add(ChatMessageDto(role = msg.role, content = msg.content))
                }

                val response = chatRepository.sendMessage(apiMessages)
                val assistantMessage = ChatMessage(role = "assistant", content = response)
                _uiState.update {
                    it.copy(
                        messages = it.messages + assistantMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.update {
                    it.copy(isLoading = false, error = errorMsg)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun observeTtsSpeaking() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                if (!speaking) {
                    _uiState.update { it.copy(speakingMessageId = null) }
                }
            }
        }
    }

    fun speakMessage(messageId: String) {
        val message = _uiState.value.messages.find { it.id == messageId } ?: return
        _uiState.update { it.copy(speakingMessageId = messageId) }
        viewModelScope.launch {
            val speed = settingsManager.ttsSpeedFlow.first()
            val pitch = settingsManager.ttsPitchFlow.first()
            ttsManager.speak(message.content, _uiState.value.language, speed, pitch)
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
        _uiState.update { it.copy(speakingMessageId = null) }
    }

    fun startListening(context: Context) {
        try {
            // Try on-device recognizer first (API 33+), then fall back to default.
            // Skip isRecognitionAvailable() check — it returns false on many Chinese ROMs
            // (Xiaomi/MIUI, etc.) even though a recognizer is actually present.
            speechRecognizer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
            ) {
                SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
            } else {
                SpeechRecognizer.createSpeechRecognizer(context)
            }
        } catch (e: Exception) {
            Log.w("ChatViewModel", "Failed to create SpeechRecognizer", e)
            _uiState.update { it.copy(error = "speech_not_available") }
            return
        }

        val language = _uiState.value.language
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (language == "zh") "zh-CN" else "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _uiState.update { it.copy(isListening = false) }
            }
            override fun onError(error: Int) {
                Log.w("ChatViewModel", "SpeechRecognizer error: $error")
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> null // silence, no speech detected
                    SpeechRecognizer.ERROR_CLIENT -> "speech_not_available"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "audio_permission_required"
                    else -> "speech_not_available"
                }
                _uiState.update { it.copy(isListening = false, error = errorMsg) }
            }
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (text != null) {
                    _uiState.update { it.copy(inputText = text, isListening = false) }
                } else {
                    _uiState.update { it.copy(isListening = false) }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (text != null) {
                    _uiState.update { it.copy(inputText = text) }
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer?.startListening(intent)
        _uiState.update { it.copy(isListening = true) }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
