package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.api.TencentVisionApiService
import com.example.world_of_dinosaurs_extented.data.remote.dto.LabelAnnotation
import com.example.world_of_dinosaurs_extented.data.remote.dto.TencentVisionRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.toLabelAnnotation
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tencent Cloud Vision API implementation
 * Uses Image Tagging service (DetectLabel)
 */
@Singleton
class TencentVisionService @Inject constructor(
    private val tencentVisionApiService: TencentVisionApiService,
    private val secretIdProvider: () -> String,
    private val secretKeyProvider: () -> String
) : VisionService {

    private val moshi: Moshi = Moshi.Builder().build()

    override suspend fun analyzeImage(base64Image: String): List<LabelAnnotation> {
        val secretId = secretIdProvider()
        val secretKey = secretKeyProvider()

        if (secretId.isBlank()) {
            throw Exception("Tencent SecretId is not configured")
        }
        if (secretKey.isBlank()) {
            throw Exception("Tencent SecretKey is not configured")
        }

        // Build request payload with base64 image
        val request = TencentVisionRequest(
            imageBase64 = base64Image
        )

        val jsonAdapter: JsonAdapter<TencentVisionRequest> = moshi.adapter(TencentVisionRequest::class.java)
        val payload = jsonAdapter.toJson(request)

        // Generate TC3 signed headers
        val headers = TencentSigner.sign(secretId, secretKey, payload)

        // Make API call
        val response = tencentVisionApiService.detectLabel(request, headers)

        // Parse response
        val data = response.response
            ?: throw Exception("Empty response from Tencent Cloud Vision API")

        if (data.error != null) {
            throw Exception("Tencent Cloud Vision error [${data.error.code}]: ${data.error.message}")
        }

        return data.labels?.map { it.toLabelAnnotation() } ?: emptyList()
    }
}