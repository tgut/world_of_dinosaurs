package com.example.world_of_dinosaurs_extented.data.remote

import android.content.Context
import android.content.Intent
import com.example.world_of_dinosaurs_extented.domain.model.User
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Huawei Account Kit manager for authentication
 * Created manually via AuthModule (not Hilt-injected)
 */
class HuaweiAccountManager(context: Context) {

    private val appContext: Context = context.applicationContext
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    companion object {
        const val RC_HUAWEI_LOGIN = 1001
    }

    /**
     * Get sign-in intent to launch
     */
    fun getSignInIntent(): Intent {
        val params = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setProfile()
            .setEmail()
            .createParams()
        val authService = AccountAuthManager.getService(appContext, params)
        return authService.signInIntent
    }

    /**
     * Process login result
     */
    fun processLoginResult(data: Intent?): Result<User> {
        return try {
            val authAccount = AccountAuthManager.parseAuthResultFromIntent(data)
                ?: return Result.failure(Exception("Login cancelled or no auth data"))

            val clazz = authAccount.javaClass
            val getOpenId = clazz.getMethod("getOpenId")
            val getDisplayName = clazz.getMethod("getDisplayName")
            val getAvatarUri = clazz.getMethod("getAvatarUri")
            val getEmail = clazz.getMethod("getEmail")

            val openId = (getOpenId.invoke(authAccount) as? String) ?: ""
            val displayName = getDisplayName.invoke(authAccount) as? String
            val avatarUri = (getAvatarUri.invoke(authAccount) as? android.net.Uri)?.toString()
            val email = getEmail.invoke(authAccount) as? String

            val user = User(
                id = openId,
                provider = "huawei",
                providerId = openId,
                displayName = displayName,
                avatarUrl = avatarUri,
                email = email,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            val params = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .createParams()
            val authService = AccountAuthManager.getService(appContext, params)
            authService.signOut()
            _currentUser.value = null
        } catch (_: Exception) { }
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null

    fun clearUser() {
        _currentUser.value = null
    }
}