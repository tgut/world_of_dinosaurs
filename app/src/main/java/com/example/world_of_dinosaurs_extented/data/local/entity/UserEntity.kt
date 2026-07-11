package com.example.world_of_dinosaurs_extented.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for Room database
 * Stores user information for offline access
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val provider: String,
    val providerId: String,
    val displayName: String?,
    val avatarUrl: String?,
    val email: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Convert to domain User model
     */
    fun toUser(): com.example.world_of_dinosaurs_extented.domain.model.User {
        return com.example.world_of_dinosaurs_extented.domain.model.User(
            id = id,
            provider = provider,
            providerId = providerId,
            displayName = displayName,
            avatarUrl = avatarUrl,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}