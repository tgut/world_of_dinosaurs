package com.example.world_of_dinosaurs_extented

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class DinoApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        // Ad SDK is initialized in MainActivity after user accepts privacy consent
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "DinoApp/1.0 (Android; +https://example.com/dinoapp)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_images"))
                    .maxSizeBytes(50L * 1024 * 1024) // 50MB disk cache
                    .build()
            }
            .build()
    }
}
