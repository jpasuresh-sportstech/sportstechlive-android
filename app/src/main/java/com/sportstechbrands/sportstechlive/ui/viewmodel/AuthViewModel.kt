package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.LoginRequest
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import com.sportstechbrands.sportstechlive.data.network.SignupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    data class AuthUiState(
        val isLoading: Boolean  = false,
        val isSuccess: Boolean  = false,
        val error: String?      = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            try {
                val res = RetrofitClient.api.login(LoginRequest(email, password))
                if (res.success && res.data != null) {
                    tokenManager.accessToken  = res.data.accessToken
                    tokenManager.refreshToken = res.data.refreshToken
                    tokenManager.userName     = res.data.user.fullName
                    tokenManager.userEmail    = res.data.user.email
                    tokenManager.userLevel    = res.data.user.level
                    _state.value = AuthUiState(isSuccess = true)
                } else {
                    _state.value = AuthUiState(error = res.message ?: "Invalid credentials")
                }
            } catch (e: Exception) {
                _state.value = AuthUiState(error = "Connection failed. Check your network.")
            }
        }
    }

    fun signup(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            try {
                val res = RetrofitClient.api.signup(SignupRequest(fullName, email, password))
                if (res.success && res.data != null) {
                    tokenManager.accessToken  = res.data.accessToken
                    tokenManager.refreshToken = res.data.refreshToken
                    tokenManager.userName     = res.data.user.fullName
                    tokenManager.userEmail    = res.data.user.email
                    tokenManager.userLevel    = res.data.user.level
                    _state.value = AuthUiState(isSuccess = true)
                } else {
                    _state.value = AuthUiState(error = res.message ?: "Signup failed")
                }
            } catch (e: Exception) {
                _state.value = AuthUiState(error = "Connection failed. Check your network.")
            }
        }
    }

    fun resetState() { _state.value = AuthUiState() }
}
