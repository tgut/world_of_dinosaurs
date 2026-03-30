package com.example.world_of_dinosaurs_extented.data.remote

import com.example.world_of_dinosaurs_extented.data.remote.api.DinoApiService
import com.example.world_of_dinosaurs_extented.data.remote.dto.DinosaurDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DinosaurRemoteDataSource @Inject constructor(
    private val apiService: DinoApiService
) {
    suspend fun fetchExtendedDinosaurs(): Result<List<DinosaurDto>> {
        return try {
            val dtos = apiService.getDinosaurs(DINOSAURS_JSON_URL)
            Result.success(dtos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        // This URL should point to the hosted dinosaurs_extended.json
        // For now, we use a placeholder. Update with actual URL after hosting.
        const val DINOSAURS_JSON_URL =
            "https://raw.githubusercontent.com/user/world-of-dinosaurs/main/data/dinosaurs_extended.json"
    }
}
