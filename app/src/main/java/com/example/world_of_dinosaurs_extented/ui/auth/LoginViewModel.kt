package com.example.world_of_dinosaurs_extented.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.world_of_dinosaurs_extented.domain.model.User
import com.example.world_of_dinosaurs_extented.domain.usecase.GetUserProfileUseCase
import com.example.world_of_dinosaurs_extented.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String? = null,
    val hasPlatformLogin: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val loginIntentProvider: LoginIntentProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
        _uiState.update { it.copy(hasPlatformLogin = loginIntentProvider.getSignInIntent() != null) }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = user != null,
                        userName = user?.displayName
                    )
                }
            }
        }
    }

    /**
     * Returns the platform sign-in intent (e.g., Huawei Account Kit),
     * or null if no platform login is available (Google flavor).
     */
    fun getSignInIntent(): Intent? = loginIntentProvider.getSignInIntent()

    /**
     * Called when platform login activity returns a result.
     * Processes the result via the platform provider and persists the user.
     */
    fun handleLoginResult(data: Intent?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginIntentProvider.processLoginResult(data)
                    ?: throw Exception("Login cancelled")
                loginUseCase("huawei", mapOf(
                    "id" to user.id,
                    "displayName" to (user.displayName ?: ""),
                    "provider" to "huawei"
                ))
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        userName = user.displayName
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Login failed")
                }
            }
        }
    }

    /**
     * Simple local-only login for the Google flavor (no platform auth).
     */
    fun loginLocally() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginUseCase("local", mapOf("provider" to "local"))
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        userName = user.displayName
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Login failed")
                }
            }
        }
    }

    fun onLoginCancelled() {
        _uiState.update { it.copy(isLoading = false, error = "Login cancelled") }
    }

    fun loginWithHuawei() {
        val intent = loginIntentProvider.getSignInIntent()
        if (intent != null) {
            // Huawei flavor: intent is launched by the Composable layer
            _uiState.update { it.copy(isLoading = true, error = null) }
        } else {
            // Google flavor: log in locally
            loginLocally()
        }
    }
}