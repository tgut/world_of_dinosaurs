package com.example.world_of_dinosaurs_extented.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<ChatMessageDto>,
    @Json(name = "temperature") val temperature: Double = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int = 1024
)

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "choices") val choices: List<ChatChoice>?
)

@JsonClass(generateAdapter = true)
data class ChatChoice(
    @Json(name = "message") val message: ChatMessageDto?
)
