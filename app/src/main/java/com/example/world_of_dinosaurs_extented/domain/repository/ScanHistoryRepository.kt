package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun getAllScans(): Flow<List<ScanHistoryEntity>>
    fun getDistinctDinosaurIds(): Flow<List<String>>
    suspend fun recordScan(dinosaurId: String)
    suspend fun clearAll()
}
