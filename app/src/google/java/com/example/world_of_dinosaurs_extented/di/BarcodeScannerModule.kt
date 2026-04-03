package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.ui.qrscan.BarcodeScanner
import com.example.world_of_dinosaurs_extented.ui.qrscan.MlKitBarcodeScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BarcodeScannerModule {
    @Binds
    @Singleton
    abstract fun bindBarcodeScanner(impl: MlKitBarcodeScanner): BarcodeScanner
}
