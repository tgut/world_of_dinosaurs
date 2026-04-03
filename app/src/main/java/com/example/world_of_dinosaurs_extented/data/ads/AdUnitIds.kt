package com.example.world_of_dinosaurs_extented.data.ads

/**
 * Ad unit ID provider — implemented per flavor.
 * google flavor: AdMob test/real unit IDs
 * huawei flavor: HMS Ads test/real unit IDs
 */
interface AdUnitIds {
    val bannerDetail: String
    val rewardedQuiz: String
}
