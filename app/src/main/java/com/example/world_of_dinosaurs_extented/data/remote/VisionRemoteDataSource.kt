package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.api.VisionApiService
import com.example.world_of_dinosaurs_extented.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionRemoteDataSource @Inject constructor(
    private val visionApiService: VisionApiService
) {
    suspend fun analyzeImage(base64Image: String, apiKey: String): AnnotateImageResponse {
        val request = VisionApiRequest(
            requests = listOf(
                AnnotateImageRequest(
                    image = VisionImage(content = base64Image),
                    features = listOf(
                        VisionFeature(type = "LABEL_DETECTION", maxResults = 15),
                        VisionFeature(type = "WEB_DETECTION", maxResults = 10)
                    )
                )
            )
        )
        val response = visionApiService.annotateImage(apiKey, request)
        return response.responses?.firstOrNull()
            ?: throw Exception("Empty response from Vision API")
    }
}
