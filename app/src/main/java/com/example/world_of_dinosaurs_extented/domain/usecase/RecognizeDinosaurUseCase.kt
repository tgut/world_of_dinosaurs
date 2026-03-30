package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.RecognitionMatch
import com.example.world_of_dinosaurs_extented.domain.repository.DinoRecognitionRepository
import javax.inject.Inject

class RecognizeDinosaurUseCase @Inject constructor(
    private val repository: DinoRecognitionRepository
) {
    suspend operator fun invoke(base64Image: String): List<RecognitionMatch> {
        return repository.recognizeDinosaur(base64Image)
    }
}
