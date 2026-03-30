package com.example.world_of_dinosaurs_extented.data.repository

import com.example.world_of_dinosaurs_extented.data.asset.AssetDataSource
import com.example.world_of_dinosaurs_extented.data.local.dao.DinosaurDao
import com.example.world_of_dinosaurs_extented.data.local.mapper.toDomain
import com.example.world_of_dinosaurs_extented.data.local.mapper.toEntity
import com.example.world_of_dinosaurs_extented.data.remote.DinosaurRemoteDataSource
import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DinosaurRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val dinosaurDao: DinosaurDao,
    private val remoteDataSource: DinosaurRemoteDataSource
) : DinosaurRepository {

    @Volatile
    private var initialized = false

    override fun getDinosaurs(): Flow<List<Dinosaur>> {
        return dinosaurDao.getAllDinosaurs()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart { ensureDataLoaded() }
    }

    override fun getDinosaurById(id: String): Flow<Dinosaur?> {
        return dinosaurDao.getDinosaurById(id)
            .map { entity -> entity?.toDomain() }
            .onStart { ensureDataLoaded() }
    }

    override fun searchDinosaurs(query: String): Flow<List<Dinosaur>> {
        return dinosaurDao.searchDinosaurs(query)
            .map { entities -> entities.map { it.toDomain() } }
            .onStart { ensureDataLoaded() }
    }

    override fun filterDinosaurs(
        era: DinosaurEra?,
        diet: DinosaurDiet?,
        size: DinosaurSize?
    ): Flow<List<Dinosaur>> {
        return dinosaurDao.filterDinosaurs(era?.name, diet?.name, size?.name)
            .map { entities -> entities.map { it.toDomain() } }
            .onStart { ensureDataLoaded() }
    }

    private suspend fun ensureDataLoaded() {
        if (initialized) return
        initialized = true

        val count = dinosaurDao.getCount()
        if (count == 0) {
            seedFromAssets()
        }
        tryRefreshFromRemote()
    }

    private suspend fun seedFromAssets() {
        try {
            val bundledDtos = assetDataSource.loadDinosaurs()
            val entities = bundledDtos.map { dto ->
                dto.toEntity(dataSource = "bundled").copy(
                    lastUpdated = System.currentTimeMillis()
                )
            }
            dinosaurDao.insertAll(entities)
        } catch (_: Exception) {
            // If asset loading fails, continue — app will retry on next launch
        }
    }

    private suspend fun tryRefreshFromRemote() {
        try {
            val lastUpdate = dinosaurDao.getLastRemoteUpdateTime() ?: 0L
            val staleThreshold = 7L * 24 * 60 * 60 * 1000 // 7 days
            if (System.currentTimeMillis() - lastUpdate < staleThreshold) return

            val result = remoteDataSource.fetchExtendedDinosaurs()
            result.onSuccess { dtos ->
                val entities = dtos.map { dto ->
                    dto.toEntity(dataSource = "remote").copy(
                        lastUpdated = System.currentTimeMillis()
                    )
                }
                dinosaurDao.insertAll(entities)
            }
            // On failure: silently continue with cached/bundled data
        } catch (_: Exception) {
            // Network errors are expected when offline — ignore
        }
    }
}
