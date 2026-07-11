package com.example.world_of_dinosaurs_extented.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Tencent Cloud Vision Request DTOs
// API: ImageTagging (DetectLabel)
// Doc: https://cloud.tencent.com/document/product/865/44922
@JsonClass(generateAdapter = true)
data class TencentVisionRequest(
    @Json(name = "ImageBase64") val imageBase64: String? = null,
    @Json(name = "ImageUrl") val imageUrl: String? = null
)

// Tencent Cloud Vision Response DTOs
@JsonClass(generateAdapter = true)
data class TencentVisionResponse(
    @Json(name = "Response") val response: TencentVisionData?
)

@JsonClass(generateAdapter = true)
data class TencentVisionData(
    @Json(name = "Labels") val labels: List<TencentLabel>?,
    @Json(name = "Error") val error: TencentError?,
    @Json(name = "RequestId") val requestId: String?
)

@JsonClass(generateAdapter = true)
data class TencentLabel(
    @Json(name = "Name") val name: String,
    @Json(name = "Confidence") val confidence: Int,
    @Json(name = "FirstCategory") val firstCategory: String?,
    @Json(name = "SecondCategory") val secondCategory: String?
)

@JsonClass(generateAdapter = true)
data class TencentError(
    @Json(name = "Code") val code: String?,
    @Json(name = "Message") val message: String?
)

/**
 * Convert Tencent Label to common LabelAnnotation
 */
fun TencentLabel.toLabelAnnotation(): LabelAnnotation {
    return LabelAnnotation(
        description = name,
        score = confidence / 100f, // Convert 0-100 scale to 0-1
        mid = null // Tencent doesn't have this field
    )
}