package com.example.world_of_dinosaurs_extented.di

import com.example.world_of_dinosaurs_extented.data.SettingsManager
import com.example.world_of_dinosaurs_extented.data.remote.AutoDetectVisionService
import com.example.world_of_dinosaurs_extented.data.remote.GoogleVisionService
import com.example.world_of_dinosaurs_extented.data.remote.TencentVisionService
import com.example.world_of_dinosaurs_extented.data.remote.VisionService
import com.example.world_of_dinosaurs_extented.data.remote.api.DinoApiService
import com.example.world_of_dinosaurs_extented.data.remote.api.TencentVisionApiService
import com.example.world_of_dinosaurs_extented.data.remote.api.VisionApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VisionRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TencentVisionRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "DinoApp/1.0 (Android)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideDinoApiService(retrofit: Retrofit): DinoApiService {
        return retrofit.create(DinoApiService::class.java)
    }

    @VisionRetrofit
    @Provides
    @Singleton
    fun provideVisionRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideVisionApiService(@VisionRetrofit retrofit: Retrofit): VisionApiService {
        return retrofit.create(VisionApiService::class.java)
    }

    @TencentVisionRetrofit
    @Provides
    @Singleton
    fun provideTencentVisionRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://iai.tencentcloudapi.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideTencentVisionApiService(@TencentVisionRetrofit retrofit: Retrofit): TencentVisionApiService {
        return retrofit.create(TencentVisionApiService::class.java)
    }

    /**
     * Provide the VisionService based on user preference
     */
    @Provides
    @Singleton
    fun provideVisionService(
        googleVisionService: GoogleVisionService,
        tencentVisionService: TencentVisionService,
        autoDetectVisionService: AutoDetectVisionService,
        settingsManager: SettingsManager
    ): VisionService {
        // Note: This is a simplified version that always returns auto-detect
        // In a real app, you might want to re-create the service when settings change
        return autoDetectVisionService
    }

    /**
     * Provide Google Vision API key
     */
    @Provides
    @Singleton
    @Named("googleVisionApiKey")
    fun provideGoogleVisionApiKey(settingsManager: SettingsManager): () -> String {
        return {
            runBlocking { settingsManager.getVisionApiKey() }
        }
    }

    /**
     * Provide Tencent SecretId
     */
    @Provides
    @Singleton
    @Named("tencentSecretId")
    fun provideTencentSecretId(settingsManager: SettingsManager): () -> String {
        return {
            runBlocking { settingsManager.getTencentSecretId().first() }
        }
    }

    /**
     * Provide Tencent SecretKey
     */
    @Provides
    @Singleton
    @Named("tencentSecretKey")
    fun provideTencentSecretKey(settingsManager: SettingsManager): () -> String {
        return {
            runBlocking { settingsManager.getTencentSecretKey().first() }
        }
    }
}