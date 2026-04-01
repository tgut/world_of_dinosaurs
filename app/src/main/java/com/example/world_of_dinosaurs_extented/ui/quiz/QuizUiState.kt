package com.example.world_of_dinosaurs_extented.ui.quiz

import com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion
import com.example.world_of_dinosaurs_extented.domain.model.QuizResult

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val selectedAnswer: Int? = null,
    val isAnswered: Boolean = false,
    val isComplete: Boolean = false,
    val isLoading: Boolean = true,
    val language: String = "en",
    val result: QuizResult? = null,
    /** 用户看完激励视频后解锁答案解析 */
    val analysisUnlocked: Boolean = false,
    /** 激励视频广告加载中 */
    val isLoadingAd: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentIndex + 1).toFloat() / questions.size
}
