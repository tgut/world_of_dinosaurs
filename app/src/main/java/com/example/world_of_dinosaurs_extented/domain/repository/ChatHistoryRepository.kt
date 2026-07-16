package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.data.local.entity.ChatMessageEntity
import com.example.world_of_dinosaurs_extented.data.local.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
    fun getAllSessions(): Flow<List<ChatSessionEntity>>
    fun getMessages(sessionId: Long): Flow<List<ChatMessageEntity>>
    suspend fun createSession(dinosaurId: String? = null): Long
    suspend fun addMessage(sessionId: Long, role: String, content: String): Long
    suspend fun updateSessionTitle(sessionId: Long, title: String)
    suspend fun deleteSession(sessionId: Long)
    suspend fun getSession(sessionId: Long): ChatSessionEntity?
}
