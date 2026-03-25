package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.Achievement
import com.sportstechbrands.sportstechlive.data.WorkoutRepository
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.ApiStats
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    data class ProfileUiState(
        val isLoading: Boolean          = true,
        val isLoggedOut: Boolean        = false,
        val fullName: String            = "",
        val email: String               = "",
        val level: String               = "",
        val initials: String            = "",
        val stats: ApiStats             = ApiStats(),
        val achievements: List<Achievement> = emptyList(),
        val error: String?              = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        RetrofitClient.init { tokenManager.accessToken }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ProfileUiState(isLoading = true)
            try {
                val profileRes = RetrofitClient.api.getProfile()
                val achRes     = RetrofitClient.api.getAchievements()

                val user = profileRes.data
                if (user != null) {
                    val initials = user.fullName.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .joinToString("") { it.first().uppercase() }

                    val achievements = achRes.data?.map { it.toUiModel() }
                        ?: WorkoutRepository.achievements

                    _state.value = ProfileUiState(
                        isLoading    = false,
                        fullName     = user.fullName,
                        email        = user.email,
                        level        = user.level.replaceFirstChar { it.uppercase() },
                        initials     = initials,
                        stats        = user.stats,
                        achievements = achievements
                    )
                } else {
                    throw Exception("Empty profile response")
                }
            } catch (e: Exception) {
                // Use cached token info as fallback
                val cached = tokenManager.userName ?: "Athlete"
                val initials = cached.split(" ").filter { it.isNotBlank() }.take(2)
                    .joinToString("") { it.first().uppercase() }
                _state.value = ProfileUiState(
                    isLoading    = false,
                    fullName     = cached,
                    email        = tokenManager.userEmail ?: "",
                    level        = tokenManager.userLevel?.replaceFirstChar { it.uppercase() } ?: "",
                    initials     = initials,
                    achievements = WorkoutRepository.achievements,
                    error        = "Offline mode"
                )
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        _state.value = _state.value.copy(isLoggedOut = true)
    }
}
