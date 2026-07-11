package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user data management
 */
interface UserRepository {

    /**
     * Get current user
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean

    /**
     * Login user with given credentials
     */
    suspend fun login(provider: String, credentials: Map<String, Any>): User

    /**
     * Logout current user
     */
    suspend fun logout()

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(profileUpdate: UserProfileUpdate): User

    /**
     * Get user ID
     */
    fun getUserId(): String?
}

/**
 * Profile update data class
 */
data class UserProfileUpdate(
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val email: String? = null
)