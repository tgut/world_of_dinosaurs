package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.UserDao
import com.example.world_of_dinosaurs_extented.data.local.entity.UserEntity
import com.example.world_of_dinosaurs_extented.data.remote.HuaweiAccountManager
import com.example.world_of_dinosaurs_extented.domain.model.User
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserProfileUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Huawei-specific UserRepository implementation
 * Uses HuaweiAccountManager for remote authentication
 */
class UserRepositoryImpl(
    private val userDao: UserDao,
    private val huaweiAccountManager: HuaweiAccountManager
) : UserRepository {

    private var currentUserId: String? = null

    override fun getCurrentUser(): Flow<User?> {
        return huaweiAccountManager.currentUser.map { remoteUser ->
            remoteUser ?: currentUserId?.let { loadLocalUser(it) }
        }
    }

    override fun isLoggedIn(): Boolean {
        return currentUserId != null || huaweiAccountManager.isLoggedIn()
    }

    override suspend fun login(provider: String, credentials: Map<String, Any>): User {
        throw UnsupportedOperationException("Use HuaweiAccountManager.startLogin() instead")
    }

    override suspend fun completeExternalLogin(user: User): User {
        saveUser(user)
        return user
    }

    override suspend fun logout() {
        huaweiAccountManager.logout()
        currentUserId?.let { userId ->
            userDao.getUserById(userId)?.let { userDao.deleteUser(it) }
            currentUserId = null
        }
    }

    override suspend fun updateUserProfile(profileUpdate: UserProfileUpdate): User {
        val userId = currentUserId ?: throw Exception("No user logged in")
        val currentUser = loadLocalUser(userId) ?: throw Exception("User not found")
        val updatedUser = currentUser.copy(
            displayName = profileUpdate.displayName ?: currentUser.displayName,
            avatarUrl = profileUpdate.avatarUrl ?: currentUser.avatarUrl,
            email = profileUpdate.email ?: currentUser.email,
            updatedAt = System.currentTimeMillis()
        )
        saveUser(updatedUser)
        return updatedUser
    }

    override fun getUserId(): String? = currentUserId

    suspend fun saveUser(user: User) {
        val entity = UserEntity(
            id = user.id,
            provider = user.provider,
            providerId = user.providerId,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            email = user.email,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        userDao.insertUser(entity)
        currentUserId = user.id
    }

    private suspend fun loadLocalUser(id: String): User? {
        return userDao.getUserById(id)?.toUser()
    }
}