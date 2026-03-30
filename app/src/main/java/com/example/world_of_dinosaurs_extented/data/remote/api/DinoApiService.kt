package com.example.world_of_dinosaurs_extented.data.remote.api

import com.example.world_of_dinosaurs_extented.data.remote.dto.DinosaurDto
import retrofit2.http.GET
import retrofit2.http.Url

interface DinoApiService {
    @GET
    suspend fun getDinosaurs(@Url url: String): List<DinosaurDto>
}
