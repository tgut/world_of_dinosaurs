package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.*
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetReviewQuizUseCase @Inject constructor(
    private val dinosaurRepository: DinosaurRepository,
    private val scanHistoryRepository: ScanHistoryRepository
) {
    suspend operator fun invoke(count: Int = 10): List<QuizQuestion> {
        val scannedIds = scanHistoryRepository.getDistinctDinosaurIds().first()
        val allDinos = dinosaurRepository.getDinosaurs().first()
        val scannedDinos = allDinos.filter { it.id in scannedIds }.shuffled()

        if (scannedDinos.size < 3) return emptyList()

        val questions = mutableListOf<QuizQuestion>()
        var questionId = 1

        for (dino in scannedDinos.take(count)) {
            val questionType = (questionId % 3)
            val question = when (questionType) {
                0 -> generateEraQuestion(dino, allDinos, questionId)
                1 -> generateDietQuestion(dino, allDinos, questionId)
                else -> generateSizeQuestion(dino, allDinos, questionId)
            }
            if (question != null) {
                questions.add(question)
                questionId++
            }
        }

        return questions.shuffled().take(count)
    }

    private fun generateEraQuestion(dino: Dinosaur, allDinos: List<Dinosaur>, id: Int): QuizQuestion {
        val correctAnswer = dino.era.name.lowercase().replaceFirstChar { it.uppercase() }
        val correctAnswerZh = when (dino.era) {
            DinosaurEra.TRIASSIC -> "三叠纪"
            DinosaurEra.JURASSIC -> "侏罗纪"
            DinosaurEra.CRETACEOUS -> "白垩纪"
        }
        val options = DinosaurEra.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        val optionsZh = listOf("三叠纪", "侏罗纪", "白垩纪")
        val correctIndex = DinosaurEra.entries.indexOf(dino.era)

        return QuizQuestion(
            id = "review_$id",
            question = "Which era does ${dino.name} belong to?",
            questionZh = "${dino.nameZh}属于哪个纪？",
            imageUrl = null,
            options = options,
            optionsZh = optionsZh,
            correctIndex = correctIndex,
            explanation = "${dino.name} lived during the ${correctAnswer} period.",
            explanationZh = "${dino.nameZh}生活在${correctAnswerZh}。"
        )
    }

    private fun generateDietQuestion(dino: Dinosaur, allDinos: List<Dinosaur>, id: Int): QuizQuestion {
        val options = DinosaurDiet.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        val optionsZh = DinosaurDiet.entries.map { diet ->
            when (diet) {
                DinosaurDiet.HERBIVORE -> "草食性"
                DinosaurDiet.CARNIVORE -> "肉食性"
                DinosaurDiet.OMNIVORE -> "杂食性"
                DinosaurDiet.PISCIVORE -> "鱼食性"
            }
        }
        val correctIndex = DinosaurDiet.entries.indexOf(dino.diet)
        val correctZh = optionsZh[correctIndex]

        return QuizQuestion(
            id = "review_$id",
            question = "What is the diet type of ${dino.name}?",
            questionZh = "${dino.nameZh}的食性是什么？",
            imageUrl = null,
            options = options,
            optionsZh = optionsZh,
            correctIndex = correctIndex,
            explanation = "${dino.name} is a ${options[correctIndex].lowercase()}.",
            explanationZh = "${dino.nameZh}是${correctZh}恐龙。"
        )
    }

    private fun generateSizeQuestion(dino: Dinosaur, allDinos: List<Dinosaur>, id: Int): QuizQuestion {
        val options = DinosaurSize.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        val optionsZh = DinosaurSize.entries.map { size ->
            when (size) {
                DinosaurSize.SMALL -> "小型"
                DinosaurSize.MEDIUM -> "中型"
                DinosaurSize.LARGE -> "大型"
                DinosaurSize.GIGANTIC -> "巨型"
            }
        }
        val correctIndex = DinosaurSize.entries.indexOf(dino.size)
        val correctZh = optionsZh[correctIndex]

        return QuizQuestion(
            id = "review_$id",
            question = "What is the size category of ${dino.name}?",
            questionZh = "${dino.nameZh}属于什么体型？",
            imageUrl = null,
            options = options,
            optionsZh = optionsZh,
            correctIndex = correctIndex,
            explanation = "${dino.name} is classified as ${options[correctIndex].lowercase()} sized.",
            explanationZh = "${dino.nameZh}属于${correctZh}恐龙。"
        )
    }
}
