package com.example.world_of_dinosaurs_extented

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class DinoApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        // 初始化 Google AdMob
        // App ID 在 AndroidManifest.xml 中配置
        // 注册地址: https://admob.google.com/
        MobileAds.initialize(this) {}
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
