package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.ui.model3d.ar.AREngineSceneController
import com.example.world_of_dinosaurs_extented.ui.model3d.ar.ARSceneController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ARModule {
    @Binds
    @Singleton
    abstract fun bindARSceneController(impl: AREngineSceneController): ARSceneController
}
