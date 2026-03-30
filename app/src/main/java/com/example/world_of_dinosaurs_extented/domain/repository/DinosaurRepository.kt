package com.example.world_of_dinosaurs_extented.domain.repository

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize
import kotlinx.coroutines.flow.Flow

interface DinosaurRepository {
    fun getDinosaurs(): Flow<List<Dinosaur>>
    fun getDinosaurById(id: String): Flow<Dinosaur?>
    fun searchDinosaurs(query: String): Flow<List<Dinosaur>>
    fun filterDinosaurs(
        era: DinosaurEra? = null,
        diet: DinosaurDiet? = null,
        size: DinosaurSize? = null
    ): Flow<List<Dinosaur>>
}
