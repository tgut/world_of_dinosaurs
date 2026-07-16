package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.ChatHistoryDao
import com.example.world_of_dinosaurs_extented.data.local.entity.ChatMessageEntity
import com.example.world_of_dinosaurs_extented.data.local.entity.ChatSessionEntity
import com.example.world_of_dinosaurs_extented.domain.repository.ChatHistoryRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatHistoryRepositoryImpl @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val userRepository: UserRepository
) : ChatHistoryRepository {

    private fun currentUserId(): String = userRepository.getUserId() ?: ""

    override fun getAllSessions(): Flow<List<ChatSessionEntity>> =
        chatHistoryDao.getAllSessions(currentUserId())

    override fun getMessages(sessionId: Long): Flow<List<ChatMessageEntity>> =
        chatHistoryDao.getMessages(sessionId)

    override suspend fun createSession(dinosaurId: String?): Long {
        return chatHistoryDao.insertSession(
            ChatSessionEntity(
                userId = currentUserId(),
                dinosaurId = dinosaurId,
                title = if (dinosaurId != null) "Chat about #$dinosaurId" else "New Chat"
            )
        )
    }

    override suspend fun addMessage(sessionId: Long, role: String, content: String): Long {
        return chatHistoryDao.insertMessage(
            ChatMessageEntity(sessionId = sessionId, role = role, content = content)
        )
    }

    override suspend fun updateSessionTitle(sessionId: Long, title: String) {
        chatHistoryDao.updateSessionTitle(sessionId, title)
    }

    override suspend fun deleteSession(sessionId: Long) {
        chatHistoryDao.deleteMessages(sessionId)
        chatHistoryDao.deleteSession(ChatSessionEntity(id = sessionId))
    }

    override suspend fun getSession(sessionId: Long): ChatSessionEntity? =
        chatHistoryDao.getSession(sessionId)
}
