package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion

interface QuizRepository {
    suspend fun getQuizQuestions(count: Int = 10): List<QuizQuestion>
}
