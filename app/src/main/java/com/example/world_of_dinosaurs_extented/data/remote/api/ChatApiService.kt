package com.example.world_of_dinosaurs_extented.data.remote.api

import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatCompletionRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @POST("chat/completions")
    suspend fun chatCompletionWithApiKey(
        @Query("key") apiKey: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}
