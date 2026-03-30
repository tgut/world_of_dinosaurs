package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.ScanHistoryDao
import com.example.world_of_dinosaurs_extented.data.local.entity.ScanHistoryEntity
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanHistoryRepositoryImpl @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao
) : ScanHistoryRepository {

    override fun getAllScans(): Flow<List<ScanHistoryEntity>> = scanHistoryDao.getAll()

    override fun getDistinctDinosaurIds(): Flow<List<String>> = scanHistoryDao.getDistinctDinosaurIds()

    override suspend fun recordScan(dinosaurId: String) {
        scanHistoryDao.insert(ScanHistoryEntity(dinosaurId = dinosaurId))
    }

    override suspend fun clearAll() = scanHistoryDao.clearAll()
}
