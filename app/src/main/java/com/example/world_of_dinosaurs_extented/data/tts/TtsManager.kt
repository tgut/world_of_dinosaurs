package com.example.world_of_dinosaurs_extented.data.tts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private var initFailed = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var pendingSpeak: (() -> Unit)? = null

    // Audio focus request (API 26+) — required on MIUI/EMUI to get audio routing
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        .setOnAudioFocusChangeListener { }
        .build()

    /** All engines installed on the device. */
    private var availableEngines: List<String> = emptyList()
    private var triedEngineIndex = -1

    init {
        // Discover TTS engines via PackageManager — more reliable than probe instance
        availableEngines = discoverEngines()
        Log.d(TAG, "Discovered engines: $availableEngines")

        // Now init with default engine
        initTts(null)
    }

    /**
     * Use PackageManager to query all apps that handle TTS_SERVICE intent.
     * Falls back to well-known engine package names if query returns empty.
     */
    private fun discoverEngines(): List<String> {
        val intent = Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE)
        val pm = context.packageManager
        val resolved = try {
            pm.queryIntentServices(intent, PackageManager.MATCH_ALL)
                .mapNotNull { it.serviceInfo?.packageName }
                .distinct()
        } catch (e: Exception) {
            Log.e(TAG, "PackageManager query failed", e)
            emptyList()
        }

        if (resolved.isNotEmpty()) {
            Log.d(TAG, "PackageManager found engines: $resolved")
            return resolved
        }

        // Fallback: well-known engine packages — check if installed
        val wellKnown = listOf(
            "com.google.android.tts",           // Google TTS
            "com.iflytek.speechcloud",           // iFlytek
            "com.samsung.SMT",                   // Samsung TTS
            "com.xiaomi.mibrain.speech",         // Xiaomi AI
            "com.baidu.duersdk.opensdk",         // Baidu
            "com.huawei.hiai",                   // Huawei AI
            "com.svox.pico"                      // Pico TTS (AOSP)
        )
        val installed = wellKnown.filter { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
        Log.d(TAG, "Fallback engine check: $installed")
        return installed
    }

    private fun initTts(enginePackage: String?) {
        isInitialized = false
        initFailed = false

        val onInit = TextToSpeech.OnInitListener { status ->
            val engine = try { tts?.defaultEngine } catch (_: Exception) { null } ?: "unknown"
            Log.d(TAG, "TTS onInit status=$status engine=$engine triedIndex=$triedEngineIndex")

            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                initFailed = false

                val availableLocales = try {
                    tts?.availableLanguages?.map { it.toString() } ?: emptyList()
                } catch (_: Exception) { emptyList() }
                Log.d(TAG, "Available languages: $availableLocales")

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

                Log.d(TAG, "TTS ready with engine: $engine")
                pendingSpeak?.invoke()
                pendingSpeak = null
            } else {
                Log.e(TAG, "TTS init FAILED engine=$engine")
                // Post to next loop iteration to avoid recursive shutdown inside onInit
                mainHandler.post { tryNextEngine() }
            }
        }

        // Create new instance — do NOT shutdown old one here, it may still be
        // delivering this very callback. Let GC handle it, or shutdown in tryNextEngine.
        tts = if (enginePackage != null) {
            TextToSpeech(context, onInit, enginePackage)
        } else {
            TextToSpeech(context, onInit)
        }
    }

    private fun tryNextEngine() {
        // Shutdown previous failed instance safely
        try { tts?.shutdown() } catch (_: Exception) {}
        tts = null

        triedEngineIndex++
        if (triedEngineIndex < availableEngines.size) {
            val nextEngine = availableEngines[triedEngineIndex]
            Log.d(TAG, "Trying engine [$triedEngineIndex]: $nextEngine")
            initTts(nextEngine)
        } else {
            isInitialized = false
            initFailed = true
            Log.e(TAG, "All TTS engines failed. engines=$availableEngines")
            pendingSpeak?.invoke()
            pendingSpeak = null
        }
    }

    fun speak(text: String, language: String, speed: Float = 1.0f, pitch: Float = 1.0f) {
        if (text.isBlank()) {
            Log.w(TAG, "speak() skipped: text is blank")
            return
        }

        if (!isInitialized && !initFailed) {
            Log.w(TAG, "speak() called before TTS ready — queuing")
            pendingSpeak = { speak(text, language, speed, pitch) }
            return
        }

        if (initFailed || tts == null) {
            Log.e(TAG, "speak() failed: TTS not available")
            return
        }

        val locale = if (language == "zh") {
            val candidates = listOf(
                Locale.SIMPLIFIED_CHINESE,
                Locale.TRADITIONAL_CHINESE,
                Locale("zh", "HK"),
                Locale.ENGLISH
            )
            candidates.firstOrNull { loc ->
                val r = tts!!.isLanguageAvailable(loc)
                Log.d(TAG, "isLanguageAvailable($loc) = $r")
                r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
            } ?: Locale.ENGLISH
        } else {
            Locale.ENGLISH
        }

        val langResult = tts?.setLanguage(locale)
        Log.d(TAG, "setLanguage locale=$locale result=$langResult")

        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "Language $locale not supported")
        }

        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)

        val focusResult = audioManager.requestAudioFocus(focusRequest)
        Log.d(TAG, "requestAudioFocus result=$focusResult")

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
        }
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        val engine = try { tts?.defaultEngine } catch (_: Exception) { null } ?: "unknown"
        Log.d(TAG, "speak() result=$result engine=$engine utteranceId=$utteranceId")

        if (result == TextToSpeech.ERROR) {
            Log.e(TAG, "TTS speak()=ERROR engine=$engine")
        } else {
            _isSpeaking.value = true

            // Watchdog: reset if engine stays silent for 3s
            mainHandler.postDelayed({
                if (_isSpeaking.value && tts?.isSpeaking == false) {
                    Log.w(TAG, "Watchdog: engine silent")
                    _isSpeaking.value = false
                    audioManager.abandonAudioFocusRequest(focusRequest)
                }
            }, 3000)
        }
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
