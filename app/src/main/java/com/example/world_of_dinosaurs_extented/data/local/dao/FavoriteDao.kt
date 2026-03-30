package com.example.world_of_dinosaurs_extented.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.world_of_dinosaurs_extented.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT dinosaurId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dinosaurId = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dinosaurId = :id")
    suspend fun removeFavorite(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dinosaurId = :id)")
    suspend fun isFavoriteSync(id: String): Boolean

    @Query("DELETE FROM favorites")
    suspend fun clearAll()
}
