package com.example.world_of_dinosaurs_extented.data.ads

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAdUnitIds @Inject constructor() : AdUnitIds {
    // Test IDs — replace with real AdMob unit IDs before release
    // Real format: "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
    override val bannerDetail  = "ca-app-pub-3940256099942544/6300978111"
    override val rewardedQuiz  = "ca-app-pub-3940256099942544/5224354917"
}
