package com.example.world_of_dinosaurs_extented.data.local.dao

import androidx.room.*
import com.example.world_of_dinosaurs_extented.data.local.entity.DinosaurEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DinosaurDao {

    @Query("SELECT * FROM dinosaurs ORDER BY isFeatured DESC, name ASC")
    fun getAllDinosaurs(): Flow<List<DinosaurEntity>>

    @Query("SELECT * FROM dinosaurs WHERE id = :id")
    fun getDinosaurById(id: String): Flow<DinosaurEntity?>

    @Query(
        "SELECT * FROM dinosaurs WHERE " +
        "name LIKE '%' || :query || '%' OR " +
        "nameZh LIKE '%' || :query || '%' OR " +
        "scientificName LIKE '%' || :query || '%' " +
        "ORDER BY isFeatured DESC, name ASC"
    )
    fun searchDinosaurs(query: String): Flow<List<DinosaurEntity>>

    @Query(
        "SELECT * FROM dinosaurs WHERE " +
        "(:era IS NULL OR era = :era) AND " +
        "(:diet IS NULL OR diet = :diet) AND " +
        "(:size IS NULL OR size = :size) " +
        "ORDER BY isFeatured DESC, name ASC"
    )
    fun filterDinosaurs(era: String?, diet: String?, size: String?): Flow<List<DinosaurEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dinosaurs: List<DinosaurEntity>)

    @Query("SELECT COUNT(*) FROM dinosaurs")
    suspend fun getCount(): Int

    @Query("SELECT MAX(lastUpdated) FROM dinosaurs WHERE dataSource = 'remote'")
    suspend fun getLastRemoteUpdateTime(): Long?
}
