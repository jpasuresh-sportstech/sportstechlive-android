package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.ApiChallenge
import com.sportstechbrands.sportstechlive.data.network.ApiLeaderEntry
import com.sportstechbrands.sportstechlive.data.network.ApiPost
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(app: Application) : AndroidViewModel(app) {

    data class CommunityUiState(
        val isLoading: Boolean              = true,
        val posts: List<ApiPost>            = emptyList(),
        val leaderboard: List<ApiLeaderEntry> = emptyList(),
        val challenges: List<ApiChallenge>  = emptyList(),
        val joinedChallengeId: String?      = null,
        val error: String?                  = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(CommunityUiState())
    val state: StateFlow<CommunityUiState> = _state.asStateFlow()

    init {
        RetrofitClient.init { tokenManager.accessToken }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = CommunityUiState(isLoading = true)
            try {
                val feedDef        = RetrofitClient.api.getFeed()
                val leaderDef      = RetrofitClient.api.getLeaderboard()
                val challengesDef  = RetrofitClient.api.getChallenges()

                _state.value = CommunityUiState(
                    isLoading   = false,
                    posts       = feedDef.data ?: emptyList(),
                    leaderboard = leaderDef.data ?: emptyList(),
                    challenges  = challengesDef.data ?: emptyList()
                )
            } catch (e: Exception) {
                _state.value = CommunityUiState(
                    isLoading = false,
                    error     = "Could not load community data"
                )
            }
        }
    }

    fun joinChallenge(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.joinChallenge(id)
                _state.value = _state.value.copy(joinedChallengeId = id)
            } catch (_: Exception) { /* silent */ }
        }
    }
}
