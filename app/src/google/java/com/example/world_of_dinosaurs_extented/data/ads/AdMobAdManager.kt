package com.example.world_of_dinosaurs_extented.data.ads

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// 广告单元 ID — 请替换为在 AdMob 后台创建的真实广告单元 ID
// 注册地址: https://admob.google.com/
// 以下为测试用 ID，发布前必须换成真实 ID
object AdMobAdUnits {
    const val BANNER_DETAIL   = "ca-app-pub-3940256099942544/6300978111"  // 测试 Banner
    const val REWARDED_QUIZ   = "ca-app-pub-3940256099942544/5224354917"  // 测试 Rewarded
    // TODO: 发布前替换为真实广告单元 ID，格式: "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
}

@Singleton
class AdMobAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AdManager {

    private var cachedRewardedAd: RewardedAd? = null

    override fun initialize() {
        MobileAds.initialize(context) {}
    }

    override fun loadBanner(
        unitId: String,
        onLoaded: (View) -> Unit,
        onFailed: () -> Unit
    ) {
        val adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = unitId
            adListener = object : AdListener() {
                override fun onAdLoaded() { onLoaded(this@apply) }
                override fun onAdFailedToLoad(error: LoadAdError) { onFailed() }
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun loadRewardedAd(
        unitId: String,
        onLoaded: () -> Unit,
        onFailed: () -> Unit
    ) {
        RewardedAd.load(
            context, unitId, AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    cachedRewardedAd = ad
                    onLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    cachedRewardedAd = null
                    onFailed()
                }
            }
        )
    }

    override fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit
    ) {
        val ad = cachedRewardedAd ?: run { onDismissed(); return }
        cachedRewardedAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { onDismissed() }
            override fun onAdFailedToShowFullScreenContent(error: AdError) { onDismissed() }
        }
        ad.show(activity) { onRewarded() }
    }
}
