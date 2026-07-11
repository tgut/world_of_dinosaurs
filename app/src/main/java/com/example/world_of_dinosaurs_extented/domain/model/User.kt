package com.example.world_of_dinosaurs_extented.domain.model

data class User(
    val id: String,
    val provider: String,
    val providerId: String,
    val displayName: String?,
    val avatarUrl: String?,
    val email: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun isLoggedIn(): Boolean = id.isNotBlank()

    fun getDisplayNameOrId(): String = displayName ?: providerId.substring(0, minOf(8, providerId.length))
}