package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.DinosaurDao
import com.example.world_of_dinosaurs_extented.data.local.mapper.toDomain
import com.example.world_of_dinosaurs_extented.data.remote.VisionRemoteDataSource
import com.example.world_of_dinosaurs_extented.domain.model.RecognitionMatch
import com.example.world_of_dinosaurs_extented.domain.repository.DinoRecognitionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DinoRecognitionRepositoryImpl @Inject constructor(
    private val visionRemoteDataSource: VisionRemoteDataSource,
    private val dinosaurDao: DinosaurDao
) : DinoRecognitionRepository {

    private val dinoKeywords = listOf(
        "dinosaur", "fossil", "skeleton", "prehistoric", "reptile",
        "saurus", "raptor", "rex", "ceratops", "sauropod",
        "theropod", "ornithopod", "pterosaur", "ankylosaur",
        "stegosaur", "hadrosaur", "carnosaur", "titanosaur"
    )

    override suspend fun recognizeDinosaur(base64Image: String): List<RecognitionMatch> {
        // Analyze image using the configured Vision service
        val labels = visionRemoteDataSource.analyzeImage(base64Image)

        if (labels.isEmpty()) {
            throw Exception("No labels returned from Vision API")
        }

        // Convert to label pairs
        val labelPairs = labels.map { it.description.lowercase() to it.score }

        // Get all dinosaurs from DB
        val allDinosaurs = dinosaurDao.getAllDinosaursList().map { it.toDomain() }

        // Match labels against dinosaur names
        val matches = mutableMapOf<String, RecognitionMatch>()

        for ((label, score) in labelPairs) {
            for (dino in allDinosaurs) {
                val nameMatch = label.contains(dino.name.lowercase()) ||
                        label.contains(dino.scientificName.lowercase()) ||
                        dino.name.lowercase().contains(label) ||
                        dino.scientificName.lowercase().contains(label) ||
                        label.contains(dino.nameZh)

                if (nameMatch && !matches.containsKey(dino.id)) {
                    matches[dino.id] = RecognitionMatch(
                        dinosaur = dino,
                        confidence = score,
                        matchedLabel = label
                    )
                }
            }
        }

        // If no direct name matches, try fuzzy matching with keywords
        if (matches.isEmpty()) {
            val isDinoRelated = labelPairs.any { (label, _) ->
                dinoKeywords.any { keyword -> label.contains(keyword) }
            }
            if (isDinoRelated) {
                val suggestions = allDinosaurs
                    .filter { it.isFeatured }
                    .take(3)
                    .map { dino ->
                        RecognitionMatch(
                            dinosaur = dino,
                            confidence = 0.3f,
                            matchedLabel = "dinosaur (suggested)"
                        )
                    }
                return suggestions
            }
        }

        return matches.values.sortedByDescending { it.confidence }
    }
}