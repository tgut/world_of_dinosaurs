package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.FavoriteDao
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val userRepository: UserRepository
) : FavoriteRepository {

    private fun currentUserId(): String = userRepository.getUserId() ?: ""

    override fun getFavoriteIds(): Flow<List<String>> =
        favoriteDao.getAllFavoriteIds(currentUserId())

    override fun isFavorite(dinosaurId: String): Flow<Boolean> =
        favoriteDao.isFavorite(dinosaurId, currentUserId())

    override suspend fun toggleFavorite(dinosaurId: String) {
        if (favoriteDao.isFavoriteSync(dinosaurId, currentUserId())) {
            favoriteDao.removeFavorite(dinosaurId, currentUserId())
        } else {
            favoriteDao.addFavorite(
                FavoriteEntity(dinosaurId = dinosaurId, userId = currentUserId())
            )
        }
    }

    override suspend fun clearAll() = favoriteDao.clearAll()
}
