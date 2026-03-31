package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.data.repository.ChatRepositoryImpl
import com.example.world_of_dinosaurs_extented.data.repository.DinoRecognitionRepositoryImpl
import com.example.world_of_dinosaurs_extented.data.repository.DinosaurRepositoryImpl
import com.example.world_of_dinosaurs_extented.data.repository.FavoriteRepositoryImpl
import com.example.world_of_dinosaurs_extented.data.repository.QuizRepositoryImpl
import com.example.world_of_dinosaurs_extented.data.repository.ScanHistoryRepositoryImpl
import com.example.world_of_dinosaurs_extented.domain.repository.ChatRepository
import com.example.world_of_dinosaurs_extented.domain.repository.DinoRecognitionRepository
import com.example.world_of_dinosaurs_extented.domain.repository.DinosaurRepository
import com.example.world_of_dinosaurs_extented.domain.repository.FavoriteRepository
import com.example.world_of_dinosaurs_extented.domain.repository.QuizRepository
import com.example.world_of_dinosaurs_extented.domain.repository.ScanHistoryRepository
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindDinosaurRepository(impl: DinosaurRepositoryImpl): DinosaurRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindScanHistoryRepository(impl: ScanHistoryRepositoryImpl): ScanHistoryRepository

    @Binds
    @Singleton
    abstract fun bindDinoRecognitionRepository(impl: DinoRecognitionRepositoryImpl): DinoRecognitionRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder().build()
    }
}
