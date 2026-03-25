package com.sportstechbrands.sportstechlive.data

import androidx.compose.ui.graphics.Color
import com.sportstechbrands.sportstechlive.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Models
// ─────────────────────────────────────────────────────────────────────────────

data class WorkoutCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val count: Int
)

data class Workout(
    val id: String,
    val title: String,
    val category: String,
    val trainer: String,
    val durationMin: Int,
    val calories: Int,
    val difficulty: String,        // Beginner / Intermediate / Advanced
    val isLive: Boolean = false,
    val equipment: String = "No equipment",
    val rating: Float = 4.8f,
    val gradientColors: List<Color>
)

data class LiveSession(
    val id: String,
    val title: String,
    val trainer: String,
    val startTime: String,
    val durationMin: Int,
    val category: String,
    val participants: Int,
    val isLiveNow: Boolean = false,
    val gradientColors: List<Color>
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val accentColor: Color
)

data class WeeklyStat(
    val day: String,
    val minutes: Int,
    val maxMinutes: Int = 90
)

// ─────────────────────────────────────────────────────────────────────────────
// Mock Data
// ─────────────────────────────────────────────────────────────────────────────

object WorkoutRepository {

    val categories = listOf(
        WorkoutCategory("strength",    "Strength",    "🏋️", AccentCyan,   42),
        WorkoutCategory("cardio",      "Cardio",      "❤️", AccentPink,   35),
        WorkoutCategory("yoga",        "Yoga",        "🧘", AccentPurple, 28),
        WorkoutCategory("hiit",        "HIIT",        "⚡", AccentOrange, 19),
        WorkoutCategory("running",     "Running",     "🏃", AccentGreen,  24),
        WorkoutCategory("cycling",     "Cycling",     "🚴", AccentCyan,   31),
        WorkoutCategory("rowing",      "Rowing",      "🚣", AccentPurple, 17),
        WorkoutCategory("stretching",  "Stretching",  "🤸", AccentGreen,  22)
    )

    val workouts = listOf(
        Workout(
            id = "w1",
            title = "Full Body Power Blast",
            category = "strength",
            trainer = "Max Weber",
            durationMin = 45,
            calories = 380,
            difficulty = "Intermediate",
            equipment = "Dumbbells",
            rating = 4.9f,
            gradientColors = listOf(AccentCyan, AccentPurple)
        ),
        Workout(
            id = "w2",
            title = "Cardio Inferno",
            category = "cardio",
            trainer = "Sarah Klein",
            durationMin = 30,
            calories = 290,
            difficulty = "Advanced",
            equipment = "No equipment",
            rating = 4.8f,
            gradientColors = listOf(AccentPink, AccentOrange)
        ),
        Workout(
            id = "w3",
            title = "Morning Flow Yoga",
            category = "yoga",
            trainer = "Lisa Mayer",
            durationMin = 40,
            calories = 150,
            difficulty = "Beginner",
            equipment = "Yoga mat",
            rating = 4.9f,
            gradientColors = listOf(AccentPurple, AccentPink)
        ),
        Workout(
            id = "w4",
            title = "HIIT Explosion",
            category = "hiit",
            trainer = "Tom Fischer",
            durationMin = 25,
            calories = 320,
            difficulty = "Advanced",
            equipment = "No equipment",
            rating = 4.7f,
            gradientColors = listOf(AccentOrange, AccentPink)
        ),
        Workout(
            id = "w5",
            title = "5K Speed Run",
            category = "running",
            trainer = "Anna Schmidt",
            durationMin = 35,
            calories = 310,
            difficulty = "Intermediate",
            equipment = "Treadmill",
            rating = 4.8f,
            gradientColors = listOf(AccentGreen, AccentCyan)
        ),
        Workout(
            id = "w6",
            title = "Sprint Cycle Challenge",
            category = "cycling",
            trainer = "Marc Becker",
            durationMin = 50,
            calories = 420,
            difficulty = "Intermediate",
            equipment = "Speed bike",
            rating = 4.9f,
            gradientColors = listOf(AccentCyan, AccentGreen)
        ),
        Workout(
            id = "w7",
            title = "Core & Row Power",
            category = "rowing",
            trainer = "Julia Hoffmann",
            durationMin = 40,
            calories = 360,
            difficulty = "Advanced",
            equipment = "Rowing machine",
            rating = 4.7f,
            gradientColors = listOf(AccentPurple, AccentCyan)
        ),
        Workout(
            id = "w8",
            title = "Deep Stretch & Recover",
            category = "stretching",
            trainer = "Lisa Mayer",
            durationMin = 30,
            calories = 100,
            difficulty = "Beginner",
            equipment = "No equipment",
            rating = 4.9f,
            gradientColors = listOf(AccentGreen, AccentPurple)
        ),
        Workout(
            id = "w9",
            title = "Dumbbell Strength Build",
            category = "strength",
            trainer = "Max Weber",
            durationMin = 55,
            calories = 410,
            difficulty = "Intermediate",
            equipment = "Dumbbells + Bench",
            rating = 4.8f,
            gradientColors = listOf(AccentCyan, AccentOrange)
        ),
        Workout(
            id = "w10",
            title = "Tabata Burn",
            category = "hiit",
            trainer = "Tom Fischer",
            durationMin = 20,
            calories = 280,
            difficulty = "Advanced",
            equipment = "No equipment",
            rating = 4.6f,
            gradientColors = listOf(AccentOrange, AccentCyan)
        )
    )

    val liveSessions = listOf(
        LiveSession(
            id = "l1",
            title = "Live Strength Circuit",
            trainer = "Max Weber",
            startTime = "NOW",
            durationMin = 45,
            category = "Strength",
            participants = 234,
            isLiveNow = true,
            gradientColors = listOf(AccentCyan, AccentPurple)
        ),
        LiveSession(
            id = "l2",
            title = "Morning Yoga Flow",
            trainer = "Lisa Mayer",
            startTime = "10:00",
            durationMin = 40,
            category = "Yoga",
            participants = 0,
            gradientColors = listOf(AccentPurple, AccentPink)
        ),
        LiveSession(
            id = "l3",
            title = "HIIT Cardio Blast",
            trainer = "Sarah Klein",
            startTime = "11:30",
            durationMin = 30,
            category = "HIIT",
            participants = 0,
            gradientColors = listOf(AccentPink, AccentOrange)
        ),
        LiveSession(
            id = "l4",
            title = "Speed Bike Intervals",
            trainer = "Marc Becker",
            startTime = "13:00",
            durationMin = 50,
            category = "Cycling",
            participants = 0,
            gradientColors = listOf(AccentGreen, AccentCyan)
        ),
        LiveSession(
            id = "l5",
            title = "Evening Stretch & Relax",
            trainer = "Julia Hoffmann",
            startTime = "18:00",
            durationMin = 35,
            category = "Stretching",
            participants = 0,
            gradientColors = listOf(AccentPurple, AccentGreen)
        )
    )

    val achievements = listOf(
        Achievement("a1", "First Sweat",    "Complete your first workout",       "🔥", true,  AccentOrange),
        Achievement("a2", "Week Warrior",   "7 workouts in one week",            "⚔️", true,  AccentCyan),
        Achievement("a3", "Early Bird",     "Work out before 7 AM",              "🌅", true,  AccentGreen),
        Achievement("a4", "Iron Will",      "Complete 30 workouts",              "💪", true,  AccentPurple),
        Achievement("a5", "Speed Demon",    "Run 50 km total",                   "⚡", false, AccentPink),
        Achievement("a6", "Zen Master",     "20 yoga sessions",                  "🧘", false, AccentPurple),
        Achievement("a7", "Century Club",   "100 workouts completed",            "🏆", false, WarningAmber),
        Achievement("a8", "Calorie Crusher","Burn 10,000 calories total",        "🔥", false, AccentOrange)
    )

    val weeklyStats = listOf(
        WeeklyStat("Mon", 45),
        WeeklyStat("Tue", 30),
        WeeklyStat("Wed", 60),
        WeeklyStat("Thu", 0),
        WeeklyStat("Fri", 50),
        WeeklyStat("Sat", 75),
        WeeklyStat("Sun", 40)
    )
}
