package com.example.world_of_dinosaurs_extented.domain.model

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val percentage: Float = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions * 100 else 0f
) {
    val grade: String
        get() = when {
            percentage >= 90 -> "S"
            percentage >= 80 -> "A"
            percentage >= 70 -> "B"
            percentage >= 60 -> "C"
            else -> "D"
        }
}
