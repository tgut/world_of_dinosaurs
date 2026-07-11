package com.example.world_of_dinosaurs_extented.domain.usecase

import com.example.world_of_dinosaurs_extented.domain.model.User
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for user login
 */
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Login user with given provider and credentials
     */
    suspend operator fun invoke(provider: String, credentials: Map<String, Any>): User {
        return userRepository.login(provider, credentials)
    }
}