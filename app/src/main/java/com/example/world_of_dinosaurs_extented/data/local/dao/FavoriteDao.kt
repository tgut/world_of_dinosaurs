package com.example.world_of_dinosaurs_extented.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getAllFavorites(userId: String = ""): Flow<List<FavoriteEntity>>

    @Query("SELECT dinosaurId FROM favorites WHERE userId = :userId")
    fun getAllFavoriteIds(userId: String = ""): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dinosaurId = :id AND userId = :userId)")
    fun isFavorite(id: String, userId: String = ""): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dinosaurId = :id AND userId = :userId")
    suspend fun removeFavorite(id: String, userId: String = "")

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dinosaurId = :id AND userId = :userId)")
    suspend fun isFavoriteSync(id: String, userId: String = ""): Boolean

    @Query("DELETE FROM favorites")
    suspend fun clearAll()
}
