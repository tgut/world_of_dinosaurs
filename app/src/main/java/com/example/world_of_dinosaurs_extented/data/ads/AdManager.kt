package com.example.world_of_dinosaurs_extented.data.ads

import android.app.Activity
import android.view.View

/**
 * Flavor-agnostic ad manager interface.
 * - google flavor implements this with AdMob (GMS)
 * - huawei flavor implements this with HMS Ads Kit
 */
interface AdManager {

    /**
     * Initialize the underlying ad SDK.
     * Called once from Application.onCreate().
     */
    fun initialize()

    /**
     * Load a banner ad.
     * [onLoaded] receives an [android.view.View] that can be embedded via AndroidView.
     */
    fun loadBanner(
        unitId: String,
        onLoaded: (View) -> Unit,
        onFailed: () -> Unit
    )

    /**
     * Load a rewarded ad and cache it internally.
     * Call [showRewardedAd] after [onLoaded] fires to display it.
     */
    fun loadRewardedAd(
        unitId: String,
        onLoaded: () -> Unit,
        onFailed: () -> Unit
    )

    /**
     * Show the previously loaded rewarded ad.
     * Must only be called after [loadRewardedAd] fires [onLoaded].
     */
    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit
    )
}
