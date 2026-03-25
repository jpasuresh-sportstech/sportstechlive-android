package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.QuickstartRequest
import com.sportstechbrands.sportstechlive.data.network.RecentSession
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuickstartViewModel(app: Application) : AndroidViewModel(app) {

    data class QuickstartUiState(
        val isLoading: Boolean         = false,
        val isStarting: Boolean        = false,
        val sessionStarted: Boolean    = false,
        val estimatedCalories: Int     = 0,
        val recentSessions: List<RecentSession> = emptyList(),
        val error: String?             = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(QuickstartUiState())
    val state: StateFlow<QuickstartUiState> = _state.asStateFlow()

    init {
        RetrofitClient.init { tokenManager.accessToken }
        loadRecent()
    }

    fun loadRecent() {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.getRecentQuickstarts()
                _state.value = _state.value.copy(recentSessions = res.data ?: emptyList())
            } catch (_: Exception) { /* silent */ }
        }
    }

    fun startWorkout(mode: String, durationMin: Int, intensity: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isStarting = true, error = null, sessionStarted = false)
            try {
                val res = RetrofitClient.api.startQuickstart(
                    QuickstartRequest(mode, durationMin, intensity)
                )
                if (res.success && res.data != null) {
                    _state.value = _state.value.copy(
                        isStarting        = false,
                        sessionStarted    = true,
                        estimatedCalories = res.data.estimatedCalories
                    )
                    loadRecent()
                } else {
                    _state.value = _state.value.copy(
                        isStarting = false,
                        error      = res.message ?: "Failed to start session"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isStarting = false,
                    error      = "Connection failed"
                )
            }
        }
    }

    fun acknowledgeSession() { _state.value = _state.value.copy(sessionStarted = false) }
}
