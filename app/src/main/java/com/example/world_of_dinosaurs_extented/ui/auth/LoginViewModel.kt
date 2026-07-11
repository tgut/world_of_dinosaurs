package com.example.world_of_dinosaurs_extented.ui.auth

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
    val userName: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
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

    fun loginWithHuawei() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginUseCase("huawei", mapOf("provider" to "huawei"))
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
}