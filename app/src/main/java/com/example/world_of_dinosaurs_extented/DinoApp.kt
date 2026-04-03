package com.example.world_of_dinosaurs_extented

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.world_of_dinosaurs_extented.data.ads.AdManager
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class DinoApp : Application(), ImageLoaderFactory {

    @Inject lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        // Initialize the flavor-specific ad SDK (AdMob for google, HMS Ads for huawei)
        adManager.initialize()
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
            .build()
    }
}
