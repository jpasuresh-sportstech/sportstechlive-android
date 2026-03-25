package com.sportstechbrands.sportstechlive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentCyan,
                strokeWidth = 3.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 108.dp)
            ) {
                HomeTopBar(greeting = state.greeting, userName = state.userName)
                Spacer(Modifier.height(4.dp))
                QuickStatsRow(streak = state.streak, calories = state.caloriesToday, workouts = state.workoutsCompleted)
                Spacer(Modifier.height(20.dp))
                DailyMetricsCard(
                    calories          = state.caloriesToday,
                    workoutsCompleted = state.workoutsCompleted,
                    weeklyMinutes     = state.weeklyMinutes
                )
                Spacer(Modifier.height(20.dp))
                TodaysPlanSection(workouts = state.todaysPlan)
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = "Categories")
                CategoriesRow(categories = state.categories)
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = "Trending Workouts", showSeeAll = true)
                TrendingWorkouts(workouts = state.trending)
                Spacer(Modifier.height(20.dp))
                WeeklyProgressCard(
                    stats       = state.weeklyStats,
                    minutes     = state.weeklyMinutes,
                    goalPercent = state.weeklyGoalPct
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(greeting: String, userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("$greeting,", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
            Text(
                text = if (userName.isNotBlank()) "$userName 👋" else "Welcome 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
                .liquidGlass(cornerRadius = 22.dp, fillAlpha = 0.12f, borderAlpha = 0.35f)
                .clickable { }
        ) {
            Icon(Icons.Default.Notifications, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Box(modifier = Modifier.size(8.dp).background(AccentPink, CircleShape).align(Alignment.TopEnd).offset((-2).dp, 2.dp))
        }
        Spacer(Modifier.width(10.dp))
        val initials = userName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "ST" }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(AccentCyan, AccentPurple)))
                .border(2.dp, Brush.linearGradient(listOf(AccentCyan.copy(0.6f), AccentPurple.copy(0.6f))), CircleShape)
        ) {
            Text(initials, style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Stats Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickStatsRow(streak: Int, calories: Int, workouts: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickStatChip(Modifier.weight(1f), "🔥", streak.toString(),   "Day streak", AccentOrange)
        QuickStatChip(Modifier.weight(1f), "⚡", calories.toString(), "Cal burned", AccentCyan)
        QuickStatChip(Modifier.weight(1f), "🏋️", workouts.toString(), "Workouts",   AccentPurple)
    }
}

@Composable
private fun QuickStatChip(modifier: Modifier, icon: String, value: String, label: String, accentColor: Color) {
    GlassCard(modifier = modifier.height(72.dp), cornerRadius = 18.dp, fillAlpha = 0.10f, borderAlpha = 0.28f) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(accentColor.copy(0.08f), Color.Transparent))))
        Column(modifier = Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(icon, fontSize = 14.sp)
                Text(value, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            }
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Daily Metrics Card (circular rings)
// ─────────────────────────────────────────────────────────────────────────────

private data class MetricRing(
    val label: String,
    val value: String,
    val unit: String,
    val progress: Float,
    val color: Color
)

@Composable
private fun DailyMetricsCard(calories: Int, workoutsCompleted: Int, weeklyMinutes: Int) {
    val steps        = workoutsCompleted * 2500
    val kms          = steps * 0.0008f
    val minutesToday = (weeklyMinutes / 7).coerceAtLeast(if (workoutsCompleted > 0) 30 else 0)

    val metrics = listOf(
        MetricRing("Calories", calories.toString(),                              "kcal",  (calories / 800f).coerceIn(0f, 1f),     AccentOrange),
        MetricRing("Steps",    if (steps >= 1000) "${steps / 1000}k" else "$steps", "steps", (steps / 10000f).coerceIn(0f, 1f),   AccentCyan),
        MetricRing("Distance", "%.1f".format(kms),                               "km",    (kms / 8f).coerceIn(0f, 1f),            AccentPurple),
        MetricRing("Minutes",  minutesToday.toString(),                          "min",   (minutesToday / 60f).coerceIn(0f, 1f),  AccentGreen)
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        cornerRadius = 26.dp,
        fillAlpha = 0.12f
    ) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(AccentCyan.copy(0.06f), AccentPurple.copy(0.06f)))
        ))
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text(
                "Today's Activity",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                metrics.forEach { metric -> CircularMetric(metric) }
            }
        }
    }
}

@Composable
private fun CircularMetric(metric: MetricRing) {
    val animatedProgress by animateFloatAsState(
        targetValue = metric.progress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "ring_${metric.label}"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(74.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 9.dp.toPx()
                val inset  = stroke / 2f
                val arcSize = androidx.compose.ui.geometry.Size(
                    size.width - stroke, size.height - stroke
                )
                // Background track
                drawArc(
                    color      = metric.color.copy(alpha = 0.18f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    topLeft    = Offset(inset, inset),
                    size       = arcSize,
                    style      = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                // Progress arc
                drawArc(
                    color      = metric.color,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter  = false,
                    topLeft    = Offset(inset, inset),
                    size       = arcSize,
                    style      = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    metric.value,
                    style      = MaterialTheme.typography.labelLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
                Text(
                    metric.unit,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = TextTertiary,
                    fontSize = 8.sp
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            metric.label,
            style    = MaterialTheme.typography.labelSmall,
            color    = metric.color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Today's Plan
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodaysPlanSection(workouts: List<Workout>) {
    val display = workouts.ifEmpty { WorkoutRepository.workouts.take(3) }
    Column {
        SectionHeader(title = "Today's Plan")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(display.size) { i -> TodayPlanCard(workout = display[i], isCompleted = i == 0) }
        }
    }
}

@Composable
private fun TodayPlanCard(workout: Workout, isCompleted: Boolean) {
    GlassCard(
        modifier = Modifier.width(240.dp).height(100.dp),
        cornerRadius = 20.dp,
        fillAlpha = if (isCompleted) 0.15f else 0.09f,
        borderAlpha = if (isCompleted) 0.45f else 0.25f,
        onClick = {}
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(workout.gradientColors.map { it.copy(if (isCompleted) 0.15f else 0.08f) })))
        Row(modifier = Modifier.fillMaxSize().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(workout.gradientColors.map { it.copy(0.25f) }))
            ) {
                val emoji = WorkoutRepository.categories.find { it.id == workout.category }?.icon ?: "💪"
                Text(emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("${workout.durationMin} min · ${workout.calories} cal", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            }
            if (isCompleted) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp).clip(CircleShape).background(AccentGreen.copy(0.2f))) {
                    Icon(Icons.Default.Check, null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, showSeeAll: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
        if (showSeeAll) Text("See all", style = MaterialTheme.typography.labelMedium, color = AccentCyan, modifier = Modifier.clickable { })
    }
    Spacer(Modifier.height(12.dp))
}

// ─────────────────────────────────────────────────────────────────────────────
// Categories Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoriesRow(categories: List<WorkoutCategory>) {
    var selectedCategory by remember { mutableStateOf("strength") }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            GlassCard(
                modifier = Modifier.width(92.dp).height(88.dp),
                cornerRadius = 20.dp,
                fillAlpha = if (selectedCategory == cat.id) 0.18f else 0.08f,
                borderAlpha = if (selectedCategory == cat.id) 0.5f else 0.22f,
                onClick = { selectedCategory = cat.id }
            ) {
                if (selectedCategory == cat.id) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(cat.color.copy(0.22f), cat.color.copy(0.05f)))))
                }
                Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(cat.icon, fontSize = 26.sp)
                    Spacer(Modifier.height(5.dp))
                    Text(cat.name, style = MaterialTheme.typography.labelSmall, color = if (selectedCategory == cat.id) cat.color else TextSecondary, fontWeight = if (selectedCategory == cat.id) FontWeight.SemiBold else FontWeight.Normal, maxLines = 1)
                    Text("${cat.count}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextTertiary)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Trending Workouts
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrendingWorkouts(workouts: List<Workout>) {
    val display = workouts.ifEmpty { WorkoutRepository.workouts.take(6) }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(display) { workout -> WorkoutCard(workout = workout) }
    }
}

@Composable
fun WorkoutCard(workout: Workout) {
    GlassCard(modifier = Modifier.width(178.dp).height(225.dp), cornerRadius = 22.dp, onClick = {}) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(workout.gradientColors.map { it.copy(0.18f) }, start = Offset.Zero, end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))))
        Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).liquidGlass(cornerRadius = 12.dp, fillAlpha = 0.15f)) {
                    val emoji = WorkoutRepository.categories.find { it.id == workout.category }?.icon ?: "💪"
                    Text(emoji, fontSize = 20.sp)
                }
                DifficultyBadge(level = workout.difficulty)
            }
            Column {
                Text(workout.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Spacer(Modifier.height(3.dp))
                Text(workout.trainer, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStatPill("⏱", "${workout.durationMin}m")
                    MiniStatPill("🔥", "${workout.calories}")
                }
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Star, null, tint = WarningAmber, modifier = Modifier.size(12.dp))
                    Text(workout.rating.toString(), style = MaterialTheme.typography.labelSmall, color = WarningAmber)
                }
            }
        }
    }
}

@Composable
private fun MiniStatPill(icon: String, value: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GlassSurface).padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(icon, fontSize = 9.sp)
        Text(value, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Weekly Progress Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WeeklyProgressCard(stats: List<WeeklyStat>, minutes: Int, goalPercent: Float) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 24.dp) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(AccentCyan.copy(0.05f), AccentPurple.copy(0.05f)))))
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Weekly Activity", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("$minutes min this week", style = MaterialTheme.typography.bodySmall, color = AccentCyan)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AccentGreen.copy(0.15f)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                    Text("Goal ${(goalPercent * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = AccentGreen, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(6.dp))
            GlassProgressBar(progress = goalPercent, height = 6.dp)
            Spacer(Modifier.height(18.dp))
            val display = stats.ifEmpty { WorkoutRepository.weeklyStats }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                display.forEach { stat -> WeeklyBar(stat = stat) }
            }
        }
    }
}

@Composable
private fun WeeklyBar(stat: WeeklyStat) {
    val barHeight = if (stat.minutes > 0) (stat.minutes.toFloat() / stat.maxMinutes * 80).dp else 6.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(100.dp)
    ) {
        Box(
            modifier = Modifier.width(30.dp).height(barHeight)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                    if (stat.minutes > 0)
                        Brush.verticalGradient(listOf(AccentCyan, AccentPurple))
                    else
                        Brush.verticalGradient(listOf(GlassSurface, GlassSurface))
                )
        )
        Spacer(Modifier.height(6.dp))
        Text(stat.day, style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
    }
}
