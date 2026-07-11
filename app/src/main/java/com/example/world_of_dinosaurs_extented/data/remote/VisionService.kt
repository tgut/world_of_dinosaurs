package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.dto.LabelAnnotation

/**
 * Abstract interface for Vision API services
 * Supports both Google Vision and Tencent Cloud Vision
 */
interface VisionService {
    /**
     * Analyze an image and return label annotations
     * @param base64Image Base64 encoded image data
     * @return List of label annotations with descriptions and confidence scores
     */
    suspend fun analyzeImage(base64Image: String): List<LabelAnnotation>
}