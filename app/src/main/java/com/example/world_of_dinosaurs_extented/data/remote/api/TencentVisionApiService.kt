package com.example.world_of_dinosaurs_extented.data.remote.api

import com.example.world_of_dinosaurs_extented.data.remote.dto.TencentVisionRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.TencentVisionResponse
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * Tencent Cloud Vision API service
 * Base URL: https://iai.tencentcloudapi.com/
 */
interface TencentVisionApiService {

    @POST("/")
    suspend fun detectLabel(
        @Body request: TencentVisionRequest,
        @HeaderMap headers: Map<String, String>
    ): TencentVisionResponse
}