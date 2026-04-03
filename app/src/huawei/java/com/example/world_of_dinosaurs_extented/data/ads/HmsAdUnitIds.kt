package com.example.world_of_dinosaurs_extented.data.ads

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HmsAdUnitIds @Inject constructor() : AdUnitIds {
    // HMS Ads test IDs — replace with real AppGallery ad unit IDs before release
    override val bannerDetail  = "testw6vs28auh3"
    override val rewardedQuiz  = "testx9dtjwj8hp"
}
