package com.example.world_of_dinosaurs_extented.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// 广告单元 ID — 请替换为在 AdMob 后台创建的真实广告单元 ID
// 注册地址: https://admob.google.com/
// 以下为测试用 ID，发布前必须换成真实 ID
object AdMobAdUnits {
    // 测试广告单元（开发期间使用，正式发布前替换）
    const val BANNER_DETAIL   = "ca-app-pub-3940256099942544/6300978111"  // 测试 Banner
    const val REWARDED_QUIZ   = "ca-app-pub-3940256099942544/5224354917"  // 测试 Rewarded
    // TODO: 发布前替换为真实广告单元 ID，格式: "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
}

@Singleton
class AdMobAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ────────────────────────────────────────────
    // Banner 广告（320×50，自适应宽度）
    // ────────────────────────────────────────────

    /**
     * 创建并加载 Banner 广告 View
     * @param onLoaded  加载成功，返回已填充的 AdView
     * @param onFailed  加载失败
     */
    fun loadBanner(
        unitId: String = AdMobAdUnits.BANNER_DETAIL,
        onLoaded: (AdView) -> Unit,
        onFailed: () -> Unit
    ) {
        val adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = unitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    onLoaded(this@apply)
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    onFailed()
                }
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    // ────────────────────────────────────────────
    // 激励视频广告
    // ────────────────────────────────────────────

    /**
     * 加载激励视频广告
     * @param onLoaded  加载成功，返回 RewardedAd
     * @param onFailed  加载失败
     */
    fun loadRewardedAd(
        unitId: String = AdMobAdUnits.REWARDED_QUIZ,
        onLoaded: (RewardedAd) -> Unit,
        onFailed: () -> Unit
    ) {
        RewardedAd.load(
            context,
            unitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    onLoaded(ad)
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    onFailed()
                }
            }
        )
    }

    /**
     * 展示激励视频广告
     * @param activity    当前 Activity
     * @param ad          已加载好的 RewardedAd
     * @param onRewarded  用户完整观看并获得奖励
     * @param onDismissed 广告关闭
     */
    fun showRewardedAd(
        activity: Activity,
        ad: RewardedAd,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit
    ) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                onDismissed()
            }
        }
        ad.show(activity) {
            // RewardItem callback — 用户获得奖励
            onRewarded()
        }
    }
}
