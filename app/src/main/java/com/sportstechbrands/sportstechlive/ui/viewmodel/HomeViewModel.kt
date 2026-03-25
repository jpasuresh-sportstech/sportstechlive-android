package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    data class HomeUiState(
        val isLoading: Boolean               = true,
        val greeting: String                 = "Good morning",
        val userName: String                 = "",
        val streak: Int                      = 0,
        val caloriesToday: Int               = 0,
        val workoutsCompleted: Int           = 0,
        val liveBanner: LiveSession?         = null,
        val todaysPlan: List<Workout>        = emptyList(),
        val categories: List<WorkoutCategory> = WorkoutRepository.categories,
        val trending: List<Workout>          = emptyList(),
        val weeklyStats: List<WeeklyStat>    = WorkoutRepository.weeklyStats,
        val weeklyMinutes: Int               = 0,
        val weeklyGoalPct: Float             = 0f,
        val error: String?                   = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        RetrofitClient.init { tokenManager.accessToken }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val dash = RetrofitClient.api.getDashboard()
                val catRes = RetrofitClient.api.getCategories()

                val data = dash.data
                if (data != null) {
                    val liveBanner = data.liveNow.firstOrNull()?.toUiModel()
                        ?: data.upcoming.firstOrNull()?.toUiModel()

                    val maxMin = data.weeklyActivity.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 90
                    val weeklyStats = data.weeklyActivity.map { it.toUiModel(maxMin) }
                    val weeklyMinutes = weeklyStats.sumOf { it.minutes }
                    val weeklyGoalPct = (weeklyMinutes / 300f).coerceAtMost(1f)

                    // Merge server category counts with local icons/colors
                    val apiCats = catRes.data?.toUiCategories() ?: emptyList()
                    val categories = if (apiCats.isNotEmpty()) {
                        WorkoutRepository.categories.map { local ->
                            val serverCount = apiCats.find { it.id == local.id }?.count ?: local.count
                            local.copy(count = serverCount)
                        }
                    } else WorkoutRepository.categories

                    _state.value = HomeUiState(
                        isLoading         = false,
                        greeting          = data.greeting,
                        userName          = data.user.fullName,
                        streak            = data.user.stats.currentStreak,
                        caloriesToday     = data.user.stats.caloriesBurned,
                        workoutsCompleted = data.user.stats.workoutsCompleted,
                        liveBanner        = liveBanner,
                        todaysPlan        = data.todaysPlan.map { it.toUiModel() },
                        categories        = categories,
                        trending          = data.trending.map { it.toUiModel() },
                        weeklyStats       = weeklyStats,
                        weeklyMinutes     = weeklyMinutes,
                        weeklyGoalPct     = weeklyGoalPct
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = dash.toString())
                }
            } catch (e: Exception) {
                // Fallback to static data on network failure
                _state.value = HomeUiState(
                    isLoading         = false,
                    greeting          = "Good morning",
                    userName          = tokenManager.userName ?: "Athlete",
                    streak            = 12,
                    caloriesToday     = 540,
                    workoutsCompleted = 3,
                    liveBanner        = WorkoutRepository.liveSessions.firstOrNull { it.isLiveNow },
                    todaysPlan        = WorkoutRepository.workouts.take(3),
                    categories        = WorkoutRepository.categories,
                    trending          = WorkoutRepository.workouts.take(6),
                    weeklyStats       = WorkoutRepository.weeklyStats,
                    weeklyMinutes     = 300,
                    weeklyGoalPct     = 0.75f,
                    error             = "Offline — showing cached data"
                )
            }
        }
    }
}
