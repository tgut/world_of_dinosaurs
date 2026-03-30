package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurDiet
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurEra
import com.example.world_of_dinosaurs_extented.domain.model.DinosaurSize
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class FilterDinosaursUseCase @Inject constructor(
    private val dinosaurRepository: DinosaurRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(
        era: DinosaurEra? = null,
        diet: DinosaurDiet? = null,
        size: DinosaurSize? = null
    ): Flow<List<Dinosaur>> {
        return dinosaurRepository.filterDinosaurs(era, diet, size)
            .combine(favoriteRepository.getFavoriteIds()) { dinos, favIds ->
                dinos.map { it.copy(isFavorite = it.id in favIds) }
            }
    }
}
