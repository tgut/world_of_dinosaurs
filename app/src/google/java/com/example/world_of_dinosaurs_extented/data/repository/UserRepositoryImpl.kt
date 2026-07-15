package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.UserDao
import com.example.world_of_dinosaurs_extented.data.local.entity.UserEntity
import com.example.world_of_dinosaurs_extented.domain.model.User
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserProfileUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default UserRepository implementation (no Huawei dependency)
 * Uses local Room database for user storage.
 * For huawei flavor, this class is overridden by src/huawei/ version.
 */
open class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    private var currentUserId: String? = null
    private val _currentUser = MutableStateFlow<User?>(null)

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override fun isLoggedIn(): Boolean = currentUserId != null

    override suspend fun login(provider: String, credentials: Map<String, Any>): User {
        val user = User(
            id = "${provider}_${System.currentTimeMillis()}",
            provider = provider,
            providerId = (credentials["id"] as? String) ?: "",
            displayName = credentials["displayName"] as? String ?: "User",
            avatarUrl = null,
            email = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveUser(user)
        return user
    }

    override suspend fun completeExternalLogin(user: User): User {
        saveUser(user)
        return user
    }

    override suspend fun logout() {
        currentUserId?.let { userId ->
            userDao.getUserById(userId)?.let { userDao.deleteUser(it) }
            currentUserId = null
            _currentUser.value = null
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

    private suspend fun saveUser(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                provider = user.provider,
                providerId = user.providerId,
                displayName = user.displayName,
                avatarUrl = user.avatarUrl,
                email = user.email,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        )
        currentUserId = user.id
        _currentUser.value = user
    }

    private suspend fun loadLocalUser(id: String): User? {
        return userDao.getUserById(id)?.toUser()
    }
}