package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.data.ads.AdManager
import com.example.world_of_dinosaurs_extented.data.ads.AdMobAdManager
import com.example.world_of_dinosaurs_extented.data.ads.AdUnitIds
import com.example.world_of_dinosaurs_extented.data.ads.GoogleAdUnitIds
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
    abstract fun bindAdManager(impl: AdMobAdManager): AdManager

    @Binds
    @Singleton
    abstract fun bindAdUnitIds(impl: GoogleAdUnitIds): AdUnitIds
}
