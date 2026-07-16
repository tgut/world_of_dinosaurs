package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.local.dao.ScanHistoryDao
import com.example.world_of_dinosaurs_extented.data.local.entity.ScanHistoryEntity
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanHistoryRepositoryImpl @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao,
    private val userRepository: UserRepository
) : ScanHistoryRepository {

    private fun currentUserId(): String = userRepository.getUserId() ?: ""

    override fun getAllScans(): Flow<List<ScanHistoryEntity>> =
        scanHistoryDao.getAll(currentUserId())

    override fun getDistinctDinosaurIds(): Flow<List<String>> =
        scanHistoryDao.getDistinctDinosaurIds(currentUserId())

    override suspend fun recordScan(dinosaurId: String) {
        scanHistoryDao.insert(
            ScanHistoryEntity(dinosaurId = dinosaurId, userId = currentUserId())
        )
    }

    override suspend fun clearAll() = scanHistoryDao.clearAll()
}
