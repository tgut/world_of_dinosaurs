package com.example.world_of_dinosaurs_extented.data.remote

import android.util.Log
import com.example.world_of_dinosaurs_extented.data.remote.api.ChatApiService
import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatCompletionRequest
import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatMessageDto
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) {
    private var cachedBaseUrl: String? = null
    private var cachedService: ChatApiService? = null

    private fun getService(baseUrl: String): ChatApiService {
        if (cachedBaseUrl == baseUrl && cachedService != null) {
            return cachedService!!
        }
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        cachedBaseUrl = baseUrl
        cachedService = retrofit.create(ChatApiService::class.java)
        return cachedService!!
    }

    suspend fun sendMessage(
        baseUrl: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessageDto>
    ): String {
        val service = getService(baseUrl)
        val request = ChatCompletionRequest(
            model = model,
            messages = messages
        )
        try {
            val response = service.chatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )
            return response.choices?.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Empty response from AI provider")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
            Log.e("ChatRemoteDataSource", "HTTP ${e.code()}: $errorBody")
            throw IllegalStateException("API error (${e.code()}): $errorBody", e)
        }
    }
}
