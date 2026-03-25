package com.sportstechbrands.sportstechlive.data.network

import androidx.compose.ui.graphics.Color
import com.sportstechbrands.sportstechlive.data.Achievement
import com.sportstechbrands.sportstechlive.data.LiveSession
import com.sportstechbrands.sportstechlive.data.WeeklyStat
import com.sportstechbrands.sportstechlive.data.Workout
import com.sportstechbrands.sportstechlive.data.WorkoutCategory
import com.sportstechbrands.sportstechlive.ui.theme.*

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(val email: String, val password: String)
data class SignupRequest(val fullName: String, val email: String, val password: String)

data class AuthResponse(
    val success: Boolean = false,
    val data: AuthData? = null,
    val message: String? = null
)
data class AuthData(
    val accessToken: String,
    val refreshToken: String,
    val user: ApiUser
)

// ── User / Stats ──────────────────────────────────────────────────────────────

data class ApiUser(
    val _id: String = "",
    val fullName: String = "",
    val email: String = "",
    val level: String = "beginner",
    val stats: ApiStats = ApiStats()
)

data class ApiStats(
    val workoutsCompleted: Int = 0,
    val totalMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val points: Int = 0
)

// ── Workout ───────────────────────────────────────────────────────────────────

data class ApiWorkout(
    val _id: String = "",
    val title: String = "",
    val description: String = "",
    val trainer: String = "",
    val category: String = "",
    val difficulty: String = "Beginner",
    val durationMin: Int = 0,
    val calories: Int = 0,
    val rating: Float = 4.5f,
    val ratingCount: Int = 0,
    val gradientColors: List<String> = listOf("#00D4FF", "#7B2FBE")
) {
    fun toUiModel(): Workout = Workout(
        id         = _id,
        title      = title,
        category   = category,
        trainer    = trainer,
        durationMin = durationMin,
        calories   = calories,
        difficulty = difficulty,
        rating     = rating,
        gradientColors = gradientColors.map { it.toComposeColor() }
    )
}

data class WorkoutsResponse(
    val success: Boolean = false,
    val data: List<ApiWorkout>? = null
)

// ── Live Session ──────────────────────────────────────────────────────────────

data class ApiLiveSession(
    val _id: String = "",
    val title: String = "",
    val trainer: String = "",
    val durationMin: Int = 0,
    val isLiveNow: Boolean = false,
    val participants: Int = 0,
    val gradientColors: List<String> = listOf("#00D4FF", "#7B2FBE")
) {
    fun toUiModel(): LiveSession = LiveSession(
        id           = _id,
        title        = title,
        trainer      = trainer,
        startTime    = if (isLiveNow) "NOW" else "",
        durationMin  = durationMin,
        category     = "",
        participants = participants,
        isLiveNow    = isLiveNow,
        gradientColors = gradientColors.map { it.toComposeColor() }
    )
}

// ── Home Dashboard ────────────────────────────────────────────────────────────

data class DashboardResponse(
    val success: Boolean = false,
    val data: DashboardData? = null
)

data class DashboardData(
    val greeting: String = "",
    val user: ApiUser = ApiUser(),
    val liveNow: List<ApiLiveSession> = emptyList(),
    val upcoming: List<ApiLiveSession> = emptyList(),
    val trending: List<ApiWorkout> = emptyList(),
    val todaysPlan: List<ApiWorkout> = emptyList(),
    val weeklyActivity: List<ApiWeeklyStat> = emptyList()
)

data class ApiWeeklyStat(
    val day: String = "",
    val minutes: Int = 0
) {
    fun toUiModel(maxMinutes: Int = 90): WeeklyStat = WeeklyStat(day, minutes, maxMinutes)
}

// ── Categories ────────────────────────────────────────────────────────────────

data class CategoriesResponse(
    val success: Boolean = false,
    val data: List<ApiCategory>? = null
)

data class ApiCategory(
    val category: String = "",
    val count: Int = 0
)

// ── Leaderboard ───────────────────────────────────────────────────────────────

data class LeaderboardResponse(
    val success: Boolean = false,
    val data: List<ApiLeaderEntry>? = null
)

data class ApiLeaderEntry(
    val rank: Int = 0,
    val userId: String = "",
    val name: String = "",
    val avatar: String? = null,
    val points: Int = 0,
    val streak: Int = 0,
    val isCurrentUser: Boolean = false
)

// ── Challenges ────────────────────────────────────────────────────────────────

data class ChallengesResponse(
    val success: Boolean = false,
    val data: List<ApiChallenge>? = null
)

data class ApiChallenge(
    val _id: String = "",
    val title: String = "",
    val description: String = "",
    val emoji: String = "💪",
    val daysTotal: Int = 30,
    val gradientColors: List<String> = listOf("#00D4FF", "#7B2FBE"),
    val participants: List<Any> = emptyList()
)

// ── Feed ──────────────────────────────────────────────────────────────────────

data class FeedResponse(
    val success: Boolean = false,
    val data: List<ApiPost>? = null
)

data class ApiPost(
    val _id: String = "",
    val activity: String = "",
    val emoji: String = "💪",
    val stats: ApiPostStats = ApiPostStats(),
    val likes: List<String> = emptyList(),
    val comments: List<Any> = emptyList(),
    val createdAt: String = ""
)

data class ApiPostStats(
    val duration: Int = 0,
    val calories: Int = 0,
    val distance: Float = 0f
)

// ── Quickstart ────────────────────────────────────────────────────────────────

data class QuickstartRequest(
    val mode: String,
    val durationMin: Int,
    val intensity: String
)

data class QuickstartStartResponse(
    val success: Boolean = false,
    val data: QuickstartSession? = null,
    val message: String? = null
)

data class QuickstartSession(
    val sessionId: String = "",
    val estimatedCalories: Int = 0,
    val mode: String = "",
    val durationMin: Int = 0,
    val intensity: String = ""
)

data class RecentQuickstartResponse(
    val success: Boolean = false,
    val data: List<RecentSession>? = null
)

data class RecentSession(
    val _id: String = "",
    val mode: String = "",
    val durationMin: Int = 0,
    val intensity: String = "",
    val caloriesBurned: Int = 0,
    val completedAt: String? = null
)

// ── Profile ───────────────────────────────────────────────────────────────────

data class ProfileResponse(
    val success: Boolean = false,
    val data: ApiUser? = null,
    val message: String? = null
)

data class AchievementsResponse(
    val success: Boolean = false,
    val data: List<ApiAchievement>? = null
)

data class ApiAchievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "🏆",
    val unlocked: Boolean = false,
    val type: String = ""
) {
    fun toUiModel(): Achievement = Achievement(
        id          = id,
        title       = title,
        description = description,
        icon        = icon,
        unlocked    = unlocked,
        accentColor = when (type) {
            "streak" -> AccentOrange
            "strength" -> AccentCyan
            "cardio" -> AccentPink
            "milestone" -> WarningAmber
            else -> AccentPurple
        }
    )
}

data class GenericResponse(
    val success: Boolean = false,
    val message: String? = null
)

// ── Helpers ───────────────────────────────────────────────────────────────────

fun String.toComposeColor(): Color = try {
    Color(android.graphics.Color.parseColor(this))
} catch (_: Exception) {
    AccentCyan
}

fun categoryColor(cat: String): Color = when (cat.lowercase()) {
    "strength"  -> AccentCyan
    "cardio"    -> AccentPink
    "yoga"      -> AccentPurple
    "hiit"      -> AccentOrange
    "running"   -> AccentGreen
    "cycling"   -> AccentCyan
    "rowing"    -> AccentPurple
    "stretch", "stretching" -> AccentGreen
    else        -> AccentCyan
}

fun categoryIcon(cat: String): String = when (cat.lowercase()) {
    "strength"  -> "🏋️"
    "cardio"    -> "❤️"
    "yoga"      -> "🧘"
    "hiit"      -> "⚡"
    "running"   -> "🏃"
    "cycling"   -> "🚴"
    "rowing"    -> "🚣"
    "stretch", "stretching" -> "🤸"
    else        -> "💪"
}

fun List<ApiCategory>.toUiCategories(): List<WorkoutCategory> = map { api ->
    WorkoutCategory(
        id    = api.category,
        name  = api.category.replaceFirstChar { it.uppercase() },
        icon  = categoryIcon(api.category),
        color = categoryColor(api.category),
        count = api.count
    )
}
