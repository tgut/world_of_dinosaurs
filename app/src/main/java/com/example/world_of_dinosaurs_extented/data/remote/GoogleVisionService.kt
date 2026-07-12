package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.api.VisionApiService
import com.example.world_of_dinosaurs_extented.data.remote.dto.AnnotateImageRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.VisionApiRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.VisionFeature
import com.example.world_of_dinosaurs_extented.data.remote.dto.VisionImage
import com.example.world_of_dinosaurs_extented.data.remote.dto.LabelAnnotation
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Google Vision API implementation
 * Uses the existing VisionApiService
 */
@Singleton
class GoogleVisionService @Inject constructor(
    private val visionApiService: VisionApiService,
    @Named("googleVisionApiKey") private val apiKeyProvider: () -> String
) : VisionService {

    override suspend fun analyzeImage(base64Image: String): List<LabelAnnotation> {
        val apiKey = apiKeyProvider()
        if (apiKey.isBlank()) {
            throw Exception("Google Vision API Key is not configured")
        }

        val request = VisionApiRequest(
            requests = listOf(
                AnnotateImageRequest(
                    image = VisionImage(content = base64Image),
                    features = listOf(
                        VisionFeature(type = "LABEL_DETECTION", maxResults = 15)
                        // Note: WEB_DETECTION is not available in Tencent Cloud
                    )
                )
            )
        )

        val response = visionApiService.annotateImage(apiKey, request)
        val annotateResponse = response.responses?.firstOrNull()
            ?: throw Exception("Empty response from Google Vision API")

        if (annotateResponse.error != null) {
            throw Exception("Google Vision API error: ${annotateResponse.error.message}")
        }

        return annotateResponse.labelAnnotations ?: emptyList()
    }
}