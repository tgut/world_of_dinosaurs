package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.asset.AssetDataSource
import com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion
import com.example.world_of_dinosaurs_extented.domain.repository.QuizRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource
) : QuizRepository {

    override suspend fun getQuizQuestions(count: Int): List<QuizQuestion> {
        return assetDataSource.loadQuizQuestions()
            .shuffled()
            .take(count)
            .map { it.toDomain() }
    }
}
