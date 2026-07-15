package com.example.world_of_dinosaurs_extented.ui.auth

import android.content.Intent
import com.example.world_of_dinosaurs_extented.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginIntentProviderImpl @Inject constructor() : LoginIntentProvider {
    override fun getSignInIntent(): Intent? = null
    override fun processLoginResult(data: Intent?): User? = null
}
