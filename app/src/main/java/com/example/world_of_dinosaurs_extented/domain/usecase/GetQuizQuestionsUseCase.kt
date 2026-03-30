package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion
import com.example.world_of_dinosaurs_extented.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizQuestionsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(count: Int = 10): List<QuizQuestion> {
        return quizRepository.getQuizQuestions(count)
    }
}
