package com.example.world_of_dinosaurs_extented.data.ads

import android.app.Activity
import android.content.Context
import android.view.View
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// HMS Ads 广告单元 ID — 请替换为华为 AppGallery Connect 后台创建的真实 ID
// 注册地址: https://developer.huawei.com/consumer/cn/service/josp/agc/index.html
object HmsAdUnits {
    const val BANNER_DETAIL  = "testw6vs28auh3"  // 测试 Banner ID
    const val REWARDED_QUIZ  = "testx9dtjwj8hp"  // 测试 Rewarded ID
    // TODO: 发布前替换为真实广告位 ID
}

@Singleton
class HmsAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AdManager {

    private var cachedRewardedAd: RewardAd? = null

    override fun initialize() {
        HwAds.init(context)
    }

    override fun loadBanner(
        unitId: String,
        onLoaded: (View) -> Unit,
        onFailed: () -> Unit
    ) {
        val bannerView = BannerView(context).apply {
            adId = unitId
            bannerAdSize = BannerAdSize.BANNER_SIZE_320_50
            adListener = object : AdListener() {
                override fun onAdLoaded() { onLoaded(this@apply) }
                override fun onAdFailed(errorCode: Int) { onFailed() }
            }
        }
        bannerView.loadAd(AdParam.Builder().build())
    }

    override fun loadRewardedAd(
        unitId: String,
        onLoaded: () -> Unit,
        onFailed: () -> Unit
    ) {
        val rewardAd = RewardAd(context, unitId)
        rewardAd.loadAd(
            AdParam.Builder().build(),
            object : RewardAdLoadListener() {
                override fun onRewardedLoaded() {
                    cachedRewardedAd = rewardAd
                    onLoaded()
                }
                override fun onRewardAdFailedToLoad(errorCode: Int) {
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
        ad.show(activity, object : RewardAdStatusListener() {
            override fun onRewardAdClosed() { onDismissed() }
            override fun onRewardAdFailedToShow(errorCode: Int) { onDismissed() }
            override fun onRewarded(reward: com.huawei.hms.ads.reward.Reward) { onRewarded() }
        })
    }
}
