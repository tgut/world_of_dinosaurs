package com.example.world_of_dinosaurs_extented.data.remote.dto

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DinosaurDto(
    val id: String,
    val name: String,
    val nameZh: String,
    val scientificName: String,
    val description: String,
    val descriptionZh: String,
    val era: String,
    val periodYearsAgo: String,
    val diet: String,
    val size: String,
    val lengthMeters: Double?,
    val weightKg: Double?,
    val heightMeters: Double?,
    val imageUrl: String?,
    val facts: List<String>,
    val factsZh: List<String>,
    val habitat: String,
    val habitatZh: String,
    val discoveryYear: Int?,
    val discoveryLocation: String,
    val model3dUrl: String?,
    val isFeatured: Boolean = false
) {
    fun toDomain(): Dinosaur = Dinosaur(
        id = id,
        name = name,
        nameZh = nameZh,
        scientificName = scientificName,
        description = description,
        descriptionZh = descriptionZh,
        era = try { DinosaurEra.valueOf(era) } catch (e: Exception) { DinosaurEra.CRETACEOUS },
        periodYearsAgo = periodYearsAgo,
        diet = try { DinosaurDiet.valueOf(diet) } catch (e: Exception) { DinosaurDiet.HERBIVORE },
        size = try { DinosaurSize.valueOf(size) } catch (e: Exception) { DinosaurSize.MEDIUM },
        lengthMeters = lengthMeters,
        weightKg = weightKg,
        heightMeters = heightMeters,
        imageUrl = imageUrl,
        facts = facts,
        factsZh = factsZh,
        habitat = habitat,
        habitatZh = habitatZh,
        discoveryYear = discoveryYear,
        discoveryLocation = discoveryLocation,
        model3dUrl = model3dUrl,
        isFeatured = isFeatured
    )
}

@JsonClass(generateAdapter = true)
data class QuizQuestionDto(
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
    fun toDomain() = com.example.world_of_dinosaurs_extented.domain.model.QuizQuestion(
        id = id,
        question = question,
        questionZh = questionZh,
        imageUrl = imageUrl,
        options = options,
        optionsZh = optionsZh,
        correctIndex = correctIndex,
        explanation = explanation,
        explanationZh = explanationZh
    )
}
