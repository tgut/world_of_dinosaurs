package com.example.world_of_dinosaurs_extented.ui.auth

import android.content.Intent
import com.example.world_of_dinosaurs_extented.domain.model.User

/**
 * Abstraction for platform-specific login flows (e.g., Huawei Account Kit).
 * Google flavor returns null for getSignInIntent().
 * Huawei flavor returns the Huawei Account Kit sign-in intent.
 */
interface LoginIntentProvider {
    fun getSignInIntent(): Intent?
    fun processLoginResult(data: Intent?): User?
}
