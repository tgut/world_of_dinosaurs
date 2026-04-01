package com.example.world_of_dinosaurs_extented.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.ads.AdMobAdManager
import com.example.world_of_dinosaurs_extented.domain.model.QuizResult
import com.example.world_of_dinosaurs_extented.domain.usecase.GetQuizQuestionsUseCase
import com.google.android.gms.ads.rewarded.RewardedAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizQuestionsUseCase: GetQuizQuestionsUseCase,
    private val settingsManager: SettingsManager,
    val adManager: AdMobAdManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            settingsManager.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val questions = getQuizQuestionsUseCase(10)
                _uiState.update { it.copy(questions = questions, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectAnswer(index: Int) {
        val state = _uiState.value
        if (state.isAnswered) return
        val question = state.currentQuestion ?: return
        val isCorrect = index == question.correctIndex
        _uiState.update {
            it.copy(
                selectedAnswer = index,
                isAnswered = true,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
        viewModelScope.launch {
            delay(1500)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val state = _uiState.value
        if (state.currentIndex >= state.questions.size - 1) {
            _uiState.update {
                it.copy(
                    isComplete = true,
                    result = QuizResult(
                        totalQuestions = it.questions.size,
                        correctAnswers = it.correctCount
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedAnswer = null,
                    isAnswered = false,
                    analysisUnlocked = false
                )
            }
        }
    }

    fun restart() {
        _uiState.update {
            QuizUiState(language = it.language)
        }
        loadQuiz()
    }

    /** 用户点击"看广告解锁解析"按钮 — 加载激励视频广告 */
    fun requestRewardedAd(
        onLoaded: (RewardedAd) -> Unit,
        onFailed: () -> Unit
    ) {
        _uiState.update { it.copy(isLoadingAd = true) }
        adManager.loadRewardedAd(
            onLoaded = { ad ->
                _uiState.update { it.copy(isLoadingAd = false) }
                onLoaded(ad)
            },
            onFailed = {
                _uiState.update { it.copy(isLoadingAd = false) }
                onFailed()
            }
        )
    }

    /** 用户看完激励视频后调用，解锁当前题目解析 */
    fun unlockAnalysis() {
        _uiState.update { it.copy(analysisUnlocked = true) }
    }
}
