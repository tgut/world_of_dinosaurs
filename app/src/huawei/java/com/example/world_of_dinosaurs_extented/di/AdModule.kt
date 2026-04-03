package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.data.ads.AdManager
import com.example.world_of_dinosaurs_extented.data.ads.AdUnitIds
import com.example.world_of_dinosaurs_extented.data.ads.HmsAdManager
import com.example.world_of_dinosaurs_extented.data.ads.HmsAdUnitIds
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdModule {
    @Binds
    @Singleton
    abstract fun bindAdManager(impl: HmsAdManager): AdManager

    @Binds
    @Singleton
    abstract fun bindAdUnitIds(impl: HmsAdUnitIds): AdUnitIds
}
