package com.example.world_of_dinosaurs_extented.ui.auth

import android.content.Intent
import com.example.world_of_dinosaurs_extented.data.remote.HuaweiAccountManager
import com.example.world_of_dinosaurs_extented.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginIntentProviderImpl @Inject constructor(
    private val huaweiAccountManager: HuaweiAccountManager
) : LoginIntentProvider {

    override fun getSignInIntent(): Intent? {
        return huaweiAccountManager.getSignInIntent()
    }

    override fun processLoginResult(data: Intent?): User? {
        return huaweiAccountManager.processLoginResult(data).getOrNull()
    }
}
