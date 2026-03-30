package com.example.world_of_dinosaurs_extented.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Request DTOs
@JsonClass(generateAdapter = true)
data class VisionApiRequest(
    @Json(name = "requests") val requests: List<AnnotateImageRequest>
)

@JsonClass(generateAdapter = true)
data class AnnotateImageRequest(
    @Json(name = "image") val image: VisionImage,
    @Json(name = "features") val features: List<VisionFeature>
)

@JsonClass(generateAdapter = true)
data class VisionImage(
    @Json(name = "content") val content: String // Base64 encoded
)

@JsonClass(generateAdapter = true)
data class VisionFeature(
    @Json(name = "type") val type: String,
    @Json(name = "maxResults") val maxResults: Int = 10
)

// Response DTOs
@JsonClass(generateAdapter = true)
data class VisionApiResponse(
    @Json(name = "responses") val responses: List<AnnotateImageResponse>?
)

@JsonClass(generateAdapter = true)
data class AnnotateImageResponse(
    @Json(name = "labelAnnotations") val labelAnnotations: List<LabelAnnotation>?,
    @Json(name = "webDetection") val webDetection: WebDetection?,
    @Json(name = "error") val error: VisionError?
)

@JsonClass(generateAdapter = true)
data class LabelAnnotation(
    @Json(name = "description") val description: String,
    @Json(name = "score") val score: Float,
    @Json(name = "mid") val mid: String?
)

@JsonClass(generateAdapter = true)
data class WebDetection(
    @Json(name = "webEntities") val webEntities: List<WebEntity>?,
    @Json(name = "bestGuessLabels") val bestGuessLabels: List<BestGuessLabel>?
)

@JsonClass(generateAdapter = true)
data class WebEntity(
    @Json(name = "description") val description: String?,
    @Json(name = "score") val score: Float,
    @Json(name = "entityId") val entityId: String?
)

@JsonClass(generateAdapter = true)
data class BestGuessLabel(
    @Json(name = "label") val label: String?
)

@JsonClass(generateAdapter = true)
data class VisionError(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String
)
