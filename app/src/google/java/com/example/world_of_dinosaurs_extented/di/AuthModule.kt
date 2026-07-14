package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.data.local.dao.UserDao
import com.example.world_of_dinosaurs_extented.data.repository.UserRepositoryImpl
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import com.example.world_of_dinosaurs_extented.domain.usecase.GetUserProfileUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.LoginUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.LogoutUseCase
import com.example.world_of_dinosaurs_extented.ui.auth.LoginIntentProvider
import com.example.world_of_dinosaurs_extented.ui.auth.LoginIntentProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Google flavor AuthModule - provides UserRepository binding
 * Uses the main source set's UserRepositoryImpl (Room-based, no Huawei dependency)
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(userDao)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(userRepository: UserRepository): LoginUseCase {
        return LoginUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(userRepository: UserRepository): LogoutUseCase {
        return LogoutUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(userRepository: UserRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideLoginIntentProvider(impl: LoginIntentProviderImpl): LoginIntentProvider {
        return impl
    }
}