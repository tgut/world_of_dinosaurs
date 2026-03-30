package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val dinosaurRepository: DinosaurRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Dinosaur>> {
        return dinosaurRepository.getDinosaurs().combine(favoriteRepository.getFavoriteIds()) { dinos, favIds ->
            dinos.filter { it.id in favIds }.map { it.copy(isFavorite = true) }
        }
    }
}
