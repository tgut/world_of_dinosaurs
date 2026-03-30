package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.domain.model.RecognitionMatch

interface DinoRecognitionRepository {
    suspend fun recognizeDinosaur(base64Image: String): List<RecognitionMatch>
}
