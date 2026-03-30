package com.example.world_of_dinosaurs_extented.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.world_of_dinosaurs_extented.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    fun getAll(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT DISTINCT dinosaurId FROM scan_history")
    fun getDistinctDinosaurIds(): Flow<List<String>>

    @Insert
    suspend fun insert(entity: ScanHistoryEntity)

    @Query("SELECT COUNT(*) FROM scan_history WHERE dinosaurId = :id")
    suspend fun getScanCount(id: String): Int

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
