package com.sportstechbrands.sportstechlive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sportstechbrands.sportstechlive.data.Workout
import com.sportstechbrands.sportstechlive.data.WorkoutRepository
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutsViewModel(app: Application) : AndroidViewModel(app) {

    data class WorkoutsUiState(
        val isLoading: Boolean      = true,
        val workouts: List<Workout> = emptyList(),
        val error: String?          = null
    )

    private val tokenManager = TokenManager.getInstance(app)
    private val _state = MutableStateFlow(WorkoutsUiState())
    val state: StateFlow<WorkoutsUiState> = _state.asStateFlow()

    init {
        RetrofitClient.init { tokenManager.accessToken }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = WorkoutsUiState(isLoading = true)
            try {
                val res = RetrofitClient.api.getWorkouts(limit = 50)
                val workouts = res.data?.map { it.toUiModel() } ?: emptyList()
                _state.value = WorkoutsUiState(isLoading = false, workouts = workouts)
            } catch (e: Exception) {
                _state.value = WorkoutsUiState(
                    isLoading = false,
                    workouts  = WorkoutRepository.workouts,
                    error     = "Offline — showing cached data"
                )
            }
        }
    }
}
