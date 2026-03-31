package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.data.remote.dto.ChatMessageDto

interface ChatRepository {
    suspend fun sendMessage(messages: List<ChatMessageDto>): String
    fun buildSystemPrompt(language: String, dinosaurContext: String?): String
}
