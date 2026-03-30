package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(dinosaurId: String) {
        favoriteRepository.toggleFavorite(dinosaurId)
    }
}
