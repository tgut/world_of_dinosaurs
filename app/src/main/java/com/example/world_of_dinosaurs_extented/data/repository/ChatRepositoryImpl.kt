package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.ChatProvider
import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.remote.ChatRemoteDataSource
import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatMessageDto
import com.example.world_of_dinosaurs_extented.domain.repository.ChatRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatRemoteDataSource: ChatRemoteDataSource,
    private val settingsManager: SettingsManager
) : ChatRepository {

    override suspend fun sendMessage(messages: List<ChatMessageDto>): String {
        val providerKey = settingsManager.chatProviderFlow.first()
        val provider = ChatProvider.fromKey(providerKey)
        val apiKey = settingsManager.chatApiKeyFlow.first()
        val customBaseUrl = settingsManager.chatBaseUrlFlow.first()
        val customModel = settingsManager.chatModelFlow.first()

        if (apiKey.isBlank()) {
            throw IllegalStateException("API key not configured")
        }

        val baseUrl = settingsManager.getResolvedChatBaseUrl(provider, customBaseUrl)
        val model = settingsManager.getResolvedChatModel(provider, customModel)

        if (baseUrl.isBlank()) {
            throw IllegalStateException("Base URL not configured")
        }
        if (model.isBlank()) {
            throw IllegalStateException("Model not configured")
        }

        return chatRemoteDataSource.sendMessage(baseUrl, apiKey, model, messages)
    }

    override fun buildSystemPrompt(language: String, dinosaurContext: String?): String {
        val base = if (language == "zh") {
            "你是一个恐龙知识专家助手。以有趣、适合所有年龄段的方式回答关于恐龙的问题。" +
                "回答请使用中文，保持简洁（200字以内），除非用户要求更详细的回答。"
        } else {
            "You are a dinosaur expert assistant. Answer questions about dinosaurs in a fun, " +
                "educational way suitable for all ages. Keep answers concise (under 200 words) " +
                "unless asked for more detail."
        }
        return if (dinosaurContext != null) {
            "$base\n\n$dinosaurContext"
        } else {
            base
        }
    }
}
