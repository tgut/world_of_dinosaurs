package com.example.world_of_dinosaurs_extented.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.domain.model.QuizResult
import com.example.world_of_dinosaurs_extented.domain.usecase.GetQuizQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuizQuestionsUseCase: GetQuizQuestionsUseCase,
    private val settingsManager: SettingsManager
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
                    isAnswered = false
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
}
