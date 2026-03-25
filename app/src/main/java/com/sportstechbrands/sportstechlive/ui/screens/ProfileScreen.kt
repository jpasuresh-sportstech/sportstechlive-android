package com.sportstechbrands.sportstechlive.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.navigation.AuthRoutes
import com.sportstechbrands.sportstechlive.navigation.Routes
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navigate to login on logout
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate(AuthRoutes.LOGIN) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AccentCyan, strokeWidth = 3.dp)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                ProfileHeader(
                    fullName = state.fullName,
                    initials = state.initials,
                    email    = state.email,
                    level    = state.level
                )
                Spacer(Modifier.height(20.dp))
                StatsRow(
                    workouts = state.stats.workoutsCompleted,
                    minutes  = state.stats.totalMinutes,
                    streak   = state.stats.currentStreak
                )
                Spacer(Modifier.height(20.dp))
                WeeklyProgressSection()
                Spacer(Modifier.height(20.dp))
                AchievementsSection(achievements = state.achievements)
                Spacer(Modifier.height(20.dp))
                DeviceSection()
                Spacer(Modifier.height(20.dp))
                SettingsSection(onSignOut = { viewModel.logout() })
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(fullName: String, initials: String, email: String, level: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        cornerRadius = 28.dp,
        fillAlpha = 0.12f
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(AccentCyan.copy(0.1f), AccentPurple.copy(0.1f)))))
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(92.dp).background(Brush.linearGradient(listOf(AccentCyan, AccentPurple, AccentPink)), CircleShape))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(84.dp).background(Brush.linearGradient(listOf(AccentCyan.copy(0.8f), AccentPurple.copy(0.8f))), CircleShape)
                ) {
                    Text(initials.ifBlank { "ST" }, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Black)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(26.dp).align(Alignment.BottomEnd).background(AccentCyan, CircleShape).clickable { }
                ) {
                    Icon(Icons.Default.Edit, null, tint = SpaceBlack, modifier = Modifier.size(12.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(fullName.ifBlank { "Athlete" }, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(email.ifBlank { "Fitness enthusiast" }, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(AccentCyan.copy(0.2f), AccentPurple.copy(0.2f))))
                    .border(1.dp, Brush.horizontalGradient(listOf(AccentCyan.copy(0.5f), AccentPurple.copy(0.3f))), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                Text(level.ifBlank { "Pro Member" }, style = MaterialTheme.typography.labelMedium, color = AccentCyan, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stats Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(workouts: Int, minutes: Int, streak: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassStatCard(value = workouts.toString(), label = "Workouts",   accentColor = AccentCyan,   modifier = Modifier.weight(1f))
        GlassStatCard(value = "${minutes / 60}h",  label = "Total Time", accentColor = AccentPurple, modifier = Modifier.weight(1f))
        GlassStatCard(value = streak.toString(),   label = "Day Streak", accentColor = AccentGreen,  modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Weekly Progress Section (static - could be wired to API in future)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WeeklyProgressSection() {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 24.dp) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("This Week", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("Goal: 5 sessions", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("4 / 5", style = MaterialTheme.typography.titleMedium, color = AccentCyan, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp))
                GlassProgressBar(progress = 0.8f, modifier = Modifier.weight(1f), height = 10.dp)
                Spacer(Modifier.width(10.dp))
                Text("80%", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                ProfileMiniStat("Calories", "2,450 kcal", AccentOrange, "🔥")
                Box(Modifier.width(1.dp).height(40.dp).background(GlassBorderTop))
                ProfileMiniStat("Active time", "3h 40m", AccentCyan, "⏱")
                Box(Modifier.width(1.dp).height(40.dp).background(GlassBorderTop))
                ProfileMiniStat("Distance", "12.4 km", AccentGreen, "🏃")
            }
        }
    }
}

@Composable
private fun ProfileMiniStat(label: String, value: String, color: Color, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Achievements
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AchievementsSection(achievements: List<Achievement>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Achievements", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("${achievements.count { it.unlocked }} / ${achievements.size}", style = MaterialTheme.typography.bodySmall, color = AccentCyan)
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(achievements) { achievement -> AchievementBadge(achievement) }
        }
    }
}

@Composable
private fun AchievementBadge(achievement: Achievement) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(18.dp))
                .background(if (achievement.unlocked) Brush.linearGradient(listOf(achievement.accentColor.copy(0.25f), achievement.accentColor.copy(0.1f))) else Brush.linearGradient(listOf(GlassSurface, GlassSurface)))
                .border(1.dp, if (achievement.unlocked) Brush.linearGradient(listOf(achievement.accentColor.copy(0.6f), achievement.accentColor.copy(0.15f))) else Brush.linearGradient(listOf(GlassBorderBot, GlassBorderBot)), RoundedCornerShape(18.dp))
                .alpha(if (achievement.unlocked) 1f else 0.4f)
        ) {
            Text(achievement.icon, fontSize = 24.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(achievement.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = if (achievement.unlocked) TextSecondary else TextDisabled, maxLines = 1)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Device Section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeviceSection() {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 24.dp) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Connected Device", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).liquidGlass(cornerRadius = 14.dp, fillAlpha = 0.15f)) {
                    Text("🚴", fontSize = 22.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sportstech SX400 Speed Bike", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(7.dp).background(AccentGreen, CircleShape))
                        Text("Connected", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                    }
                }
                Icon(Icons.Default.ChevronRight, null, tint = TextTertiary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Settings Section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(onSignOut: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 24.dp) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf(
                Triple(Icons.Default.FitnessCenter,           "Fitness Goals",  AccentCyan),
                Triple(Icons.Default.Notifications,           "Notifications",  AccentPurple),
                Triple(Icons.Default.Language,                "Language",       AccentGreen),
                Triple(Icons.Default.CardMembership,          "Subscription",   WarningAmber),
                Triple(Icons.AutoMirrored.Filled.Logout,      "Sign Out",       ErrorRed)
            ).forEach { (icon, label, color) ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { if (label == "Sign Out") onSignOut() }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.15f))) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(label, style = MaterialTheme.typography.titleSmall, color = if (label == "Sign Out") ErrorRed else TextPrimary, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                }
                if (label != "Sign Out") HorizontalDivider(color = GlassBorderBot, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
