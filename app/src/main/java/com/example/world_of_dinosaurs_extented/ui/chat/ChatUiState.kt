package com.example.world_of_dinosaurs_extented.ui.chat

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val error: String? = null,
    val language: String = "en",
    val dinosaurContext: String? = null,
    val dinosaurName: String? = null,
    val speakingMessageId: String? = null
)
