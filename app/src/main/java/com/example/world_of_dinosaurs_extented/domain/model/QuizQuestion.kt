package com.example.world_of_dinosaurs_extented.domain.model

data class QuizQuestion(
    val id: String,
    val question: String,
    val questionZh: String,
    val imageUrl: String?,
    val options: List<String>,
    val optionsZh: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val explanationZh: String
) {
    fun getLocalizedQuestion(language: String): String =
        if (language == "zh") questionZh else question

    fun getLocalizedOptions(language: String): List<String> =
        if (language == "zh") optionsZh else options

    fun getLocalizedExplanation(language: String): String =
        if (language == "zh") explanationZh else explanation
}
