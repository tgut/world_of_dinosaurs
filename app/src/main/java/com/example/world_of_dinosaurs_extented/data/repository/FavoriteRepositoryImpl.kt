package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.FavoriteDao
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavoriteIds(): Flow<List<String>> = favoriteDao.getAllFavoriteIds()

    override fun isFavorite(dinosaurId: String): Flow<Boolean> = favoriteDao.isFavorite(dinosaurId)

    override suspend fun toggleFavorite(dinosaurId: String) {
        if (favoriteDao.isFavoriteSync(dinosaurId)) {
            favoriteDao.removeFavorite(dinosaurId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(dinosaurId = dinosaurId))
        }
    }

    override suspend fun clearAll() = favoriteDao.clearAll()
}
