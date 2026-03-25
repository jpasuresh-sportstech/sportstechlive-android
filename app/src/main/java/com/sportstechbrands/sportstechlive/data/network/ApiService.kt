package com.sportstechbrands.sportstechlive.data.network

import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse

    // ── Home ──────────────────────────────────────────────────────────────
    @GET("api/home/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("api/home/categories")
    suspend fun getCategories(): CategoriesResponse

    // ── Workouts ──────────────────────────────────────────────────────────
    @GET("api/workouts")
    suspend fun getWorkouts(
        @Query("category")   category:   String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("limit")      limit:      Int?    = null,
        @Query("page")       page:       Int?    = null
    ): WorkoutsResponse

    @GET("api/workouts/{id}")
    suspend fun getWorkout(@Path("id") id: String): WorkoutsResponse

    // ── Quickstart ────────────────────────────────────────────────────────
    @POST("api/quickstart/start")
    suspend fun startQuickstart(@Body request: QuickstartRequest): QuickstartStartResponse

    @GET("api/quickstart/recent")
    suspend fun getRecentQuickstarts(): RecentQuickstartResponse

    // ── Community ─────────────────────────────────────────────────────────
    @GET("api/community/feed")
    suspend fun getFeed(): FeedResponse

    @GET("api/community/leaderboard")
    suspend fun getLeaderboard(): LeaderboardResponse

    @GET("api/community/challenges")
    suspend fun getChallenges(): ChallengesResponse

    @GET("api/community/active")
    suspend fun getActiveUsers(): GenericResponse

    @POST("api/community/challenges/{id}/join")
    suspend fun joinChallenge(@Path("id") id: String): GenericResponse

    // ── Profile ───────────────────────────────────────────────────────────
    @GET("api/profile")
    suspend fun getProfile(): ProfileResponse

    @GET("api/profile/achievements")
    suspend fun getAchievements(): AchievementsResponse

    @GET("api/profile/stats")
    suspend fun getProfileStats(): GenericResponse
}
