package com.example.world_of_dinosaurs_extented.data.remote.api

import com.example.world_of_dinosaurs_extented.data.remote.dto.VisionApiRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.VisionApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApiService {

    @POST("v1/images:annotate")
    suspend fun annotateImage(
        @Query("key") apiKey: String,
        @Body request: VisionApiRequest
    ): VisionApiResponse
}
