package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.Dinosaur
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDinosaurDetailUseCase @Inject constructor(
    private val dinosaurRepository: DinosaurRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(id: String): Flow<Dinosaur?> {
        return dinosaurRepository.getDinosaurById(id).combine(favoriteRepository.isFavorite(id)) { dino, isFav ->
            dino?.copy(isFavorite = isFav)
        }
    }
}
