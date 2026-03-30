package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.SettingsManager
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
    private val dinosaurDao: DinosaurDao,
    private val settingsManager: SettingsManager
) : DinoRecognitionRepository {

    // Common dinosaur-related keywords to filter relevant labels
    private val dinoKeywords = listOf(
        "dinosaur", "fossil", "skeleton", "prehistoric", "reptile",
        "saurus", "raptor", "rex", "ceratops", "sauropod",
        "theropod", "ornithopod", "pterosaur", "ankylosaur",
        "stegosaur", "hadrosaur", "carnosaur", "titanosaur"
    )

    override suspend fun recognizeDinosaur(base64Image: String): List<RecognitionMatch> {
        val apiKey = settingsManager.getVisionApiKey()
        if (apiKey.isBlank()) {
            throw Exception("Vision API key not configured. Please set it in Settings.")
        }

        val response = visionRemoteDataSource.analyzeImage(base64Image, apiKey)

        response.error?.let { error ->
            throw Exception("Vision API error: ${error.message}")
        }

        // Collect all labels from both label detection and web detection
        val labels = mutableListOf<Pair<String, Float>>()

        response.labelAnnotations?.forEach { label ->
            labels.add(label.description.lowercase() to label.score)
        }

        response.webDetection?.webEntities?.forEach { entity ->
            entity.description?.let { desc ->
                labels.add(desc.lowercase() to entity.score)
            }
        }

        response.webDetection?.bestGuessLabels?.forEach { label ->
            label.label?.let { desc ->
                labels.add(desc.lowercase() to 0.8f)
            }
        }

        // Get all dinosaurs from DB
        val allDinosaurs = dinosaurDao.getAllDinosaursList().map { it.toDomain() }

        // Match labels against dinosaur names
        val matches = mutableMapOf<String, RecognitionMatch>()

        for ((label, score) in labels) {
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
            val isDinoRelated = labels.any { (label, _) ->
                dinoKeywords.any { keyword -> label.contains(keyword) }
            }
            if (isDinoRelated) {
                // Return top featured dinosaurs as suggestions
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
