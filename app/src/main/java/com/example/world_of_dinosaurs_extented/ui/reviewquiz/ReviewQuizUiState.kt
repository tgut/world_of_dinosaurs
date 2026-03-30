package com.example.world_of_dinosaurs_extented.ui.reviewquiz

import com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion
import com.example.world_of_dinosaurs_extented.domain.model.QuizResult

data class ReviewQuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val isAnswered: Boolean = false,
    val correctCount: Int = 0,
    val isComplete: Boolean = false,
    val result: QuizResult? = null,
    val isLoading: Boolean = true,
    val language: String = "en",
    val isEmpty: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentIndex + 1).toFloat() / questions.size
}
