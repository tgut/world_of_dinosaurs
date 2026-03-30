package com.example.world_of_dinosaurs_extented.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteIds(): Flow<List<String>>
    fun isFavorite(dinosaurId: String): Flow<Boolean>
    suspend fun toggleFavorite(dinosaurId: String)
    suspend fun clearAll()
}
