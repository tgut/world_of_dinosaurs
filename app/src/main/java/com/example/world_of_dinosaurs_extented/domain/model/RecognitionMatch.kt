package com.example.world_of_dinosaurs_extented.domain.model

data class RecognitionMatch(
    val dinosaur: Dinosaur,
    val confidence: Float,
    val matchedLabel: String
)
