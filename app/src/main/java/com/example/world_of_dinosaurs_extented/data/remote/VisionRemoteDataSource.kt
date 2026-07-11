package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.dto.LabelAnnotation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for Vision API
 * Uses the VisionService interface which supports both Google Vision and Tencent Cloud Vision
 */
@Singleton
class VisionRemoteDataSource @Inject constructor(
    private val visionService: VisionService
) {
    /**
     * Analyze an image using the configured Vision service
     */
    suspend fun analyzeImage(base64Image: String): List<LabelAnnotation> {
        return visionService.analyzeImage(base64Image)
    }
}