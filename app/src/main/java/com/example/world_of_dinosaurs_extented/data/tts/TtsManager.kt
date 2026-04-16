package com.example.world_of_dinosaurs_extented.data.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // If speak() is called before TTS finishes initialising, store the request
    // here and replay it once onInit fires with SUCCESS.
    private var pendingSpeak: (() -> Unit)? = null

    // Audio focus request (API 26+) — required on MIUI/EMUI to get audio routing
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        .setOnAudioFocusChangeListener { /* no-op: TTS manages its own lifecycle */ }
        .build()

    init {
        tts = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            Log.d(TAG, "TTS init status=$status isInitialized=$isInitialized")
            if (isInitialized) {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d(TAG, "onStart utteranceId=$utteranceId")
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d(TAG, "onDone utteranceId=$utteranceId")
                        _isSpeaking.value = false
                        audioManager.abandonAudioFocusRequest(focusRequest)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "onError (legacy) utteranceId=$utteranceId")
                        _isSpeaking.value = false
                        audioManager.abandonAudioFocusRequest(focusRequest)
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        Log.e(TAG, "onError utteranceId=$utteranceId errorCode=$errorCode")
                        _isSpeaking.value = false
                        audioManager.abandonAudioFocusRequest(focusRequest)
                    }
                })
                // Replay any speak() call that arrived before init completed.
                pendingSpeak?.invoke()
                pendingSpeak = null
            }
        }
    }

    fun speak(text: String, language: String, speed: Float = 1.0f, pitch: Float = 1.0f) {
        if (text.isBlank()) {
            Log.w(TAG, "speak() skipped: text is blank")
            return
        }

        // TTS engine hasn't finished initialising yet — queue the request and
        // execute it as soon as onInit fires with SUCCESS.
        if (!isInitialized) {
            Log.w(TAG, "speak() called before TTS is ready — queuing for later")
            pendingSpeak = { speak(text, language, speed, pitch) }
            return
        }

        // Resolve locale — try zh_CN first, then zh_TW, then fall back to English.
        // Locale.CHINESE (="zh") has no country code and is rejected by many OEM
        // TTS engines (MIUI, EMUI) that require "zh_CN" / "zh_TW".
        val locale = if (language == "zh") {
            val candidates = listOf(
                Locale.SIMPLIFIED_CHINESE,      // zh_CN
                Locale.TRADITIONAL_CHINESE,     // zh_TW
                Locale("zh", "HK"),             // zh_HK
                Locale.ENGLISH
            )
            candidates.firstOrNull { loc ->
                val r = tts!!.isLanguageAvailable(loc)
                r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
            } ?: Locale.ENGLISH
        } else {
            Locale.ENGLISH
        }

        val langResult = tts?.setLanguage(locale)
        Log.d(TAG, "setLanguage locale=$locale result=$langResult")

        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)

        // Request audio focus so MIUI/EMUI routes the audio to the speaker.
        val focusResult = audioManager.requestAudioFocus(focusRequest)
        Log.d(TAG, "requestAudioFocus result=$focusResult")

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
        }
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        Log.d(TAG, "speak() queued: utteranceId=$utteranceId result=$result")
    }

    fun stop() {
        pendingSpeak = null
        tts?.stop()
        _isSpeaking.value = false
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    companion object {
        private const val TAG = "TtsManager"
    }
}
