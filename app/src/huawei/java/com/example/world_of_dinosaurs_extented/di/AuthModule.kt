package com.example.world_of_dinosaurs_extented.di

import android.content.Context
import com.example.world_of_dinosaurs_extented.data.local.dao.UserDao
import com.example.world_of_dinosaurs_extented.data.remote.HuaweiAccountManager
import com.example.world_of_dinosaurs_extented.data.repository.UserRepositoryImpl
import com.example.world_of_dinosaurs_extented.domain.repository.UserRepository
import com.example.world_of_dinosaurs_extented.domain.usecase.GetUserProfileUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.LoginUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Huawei Account Kit AuthModule
 * Provides Huawei-specific authentication bindings
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideHuaweiAccountManager(
        @ApplicationContext context: Context
    ): HuaweiAccountManager {
        return HuaweiAccountManager(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        huaweiAccountManager: HuaweiAccountManager
    ): UserRepository {
        return UserRepositoryImpl(userDao, huaweiAccountManager)
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
}