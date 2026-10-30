package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE, LISTENING, THINKING, SPEAKING
}

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    var onFinalSpeechResult: ((String) -> Unit)? = null
    var onSpeechError: ((String) -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
        initSpeechRecognizer()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.LISTENING
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.THINKING
                    }
                    override fun onError(error: Int) {
                        _voiceState.value = VoiceState.IDLE
                        _partialText.value = ""
                        val errMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على الصوت، حاول ثانية."
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "خطأ في اتصال الشبكة."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "يرجى منح إذن الميكروفون."
                            else -> "حدث خطأ أثناء الاستماع."
                        }
                        onSpeechError?.invoke(errMsg)
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        _voiceState.value = VoiceState.IDLE
                        _partialText.value = ""
                        if (text.isNotBlank()) {
                            onFinalSpeechResult?.invoke(text)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _partialText.value = it
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            // Prefer Arabic if available, else default
            val arLocale = Locale("ar")
            val arAvailable = tts?.isLanguageAvailable(arLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
            if (arAvailable >= TextToSpeech.LANG_AVAILABLE) {
                tts?.language = arLocale
            } else {
                tts?.language = Locale.getDefault()
            }
            // Deep male voice effect like SIMAX web app
            tts?.setPitch(0.82f)
            tts?.setSpeechRate(1.05f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = VoiceState.SPEAKING
                }
                override fun onDone(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                }
                override fun onError(utteranceId: String?) {
                    _voiceState.value = VoiceState.IDLE
                }
            })
        }
    }

    fun startListening() {
        stopSpeaking()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-SA")
        }
        _voiceState.value = VoiceState.LISTENING
        _partialText.value = "جاري الاستماع..."
        try {
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _voiceState.value = VoiceState.IDLE
        _partialText.value = ""
    }

    fun speak(text: String) {
        if (!isTtsReady || text.isBlank()) return
        stopListening()
        // Clean markdown and code formatting before speaking
        val cleanSpeech = text
            .replace(Regex("```[\\s\\S]*?```"), "كود برمجي")
            .replace(Regex("[#*_~`>\\[\\]()]"), "")
            .trim()

        _voiceState.value = VoiceState.SPEAKING
        tts?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null, "simax_voice_utt")
    }

    fun stopSpeaking() {
        tts?.stop()
        if (_voiceState.value == VoiceState.SPEAKING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun setThinking() {
        _voiceState.value = VoiceState.THINKING
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
