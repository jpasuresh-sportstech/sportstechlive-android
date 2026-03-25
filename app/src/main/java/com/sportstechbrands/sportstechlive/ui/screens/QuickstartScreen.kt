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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.data.network.RecentSession
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.QuickstartViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

private data class WorkoutMode(
    val id: String,
    val label: String,
    val emoji: String,
    val gradientColors: List<Color>
)

private val workoutModes = listOf(
    WorkoutMode("strength",  "Strength",  "🏋️", listOf(AccentCyan,   AccentPurple)),
    WorkoutMode("hiit",      "HIIT",      "⚡", listOf(AccentPink,   AccentOrange)),
    WorkoutMode("cardio",    "Cardio",    "🏃", listOf(AccentGreen,  AccentCyan)),
    WorkoutMode("yoga",      "Yoga",      "🧘", listOf(AccentPurple, AccentPink)),
    WorkoutMode("cycling",   "Cycling",   "🚴", listOf(WarningAmber, AccentOrange)),
    WorkoutMode("stretch",   "Stretch",   "🤸", listOf(AccentCyan,   AccentGreen))
)

private val durations    = listOf(5, 10, 15, 20, 30, 45, 60)
private val intensities  = listOf("Easy", "Moderate", "Intense", "Max")

private val recentQuickStarts = listOf(
    Triple("HIIT Blast",     "20 min · Intense",  AccentPink),
    Triple("Morning Yoga",   "30 min · Easy",     AccentPurple),
    Triple("Power Cardio",   "45 min · Moderate", AccentGreen)
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun QuickstartScreen(viewModel: QuickstartViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    var selectedMode      by remember { mutableStateOf("hiit") }
    var selectedDuration  by remember { mutableStateOf(20) }
    var selectedIntensity by remember { mutableStateOf("Intense") }

    // Pulse animation for Start button
    val infiniteTransition = rememberInfiniteTransition(label = "startPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "startPulseF"
    )

    val activeMode = workoutModes.find { it.id == selectedMode } ?: workoutModes[0]

    // Session started snackbar
    if (state.sessionStarted) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            viewModel.acknowledgeSession()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 116.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ────────────────────────────────────────────────────
            QuickstartHeader()

            Spacer(Modifier.height(8.dp))

            // ── Hero card — current selection summary ─────────────────────
            HeroCard(mode = activeMode, duration = selectedDuration, intensity = selectedIntensity, pulse = pulse)

            Spacer(Modifier.height(24.dp))

            // ── Workout Mode grid ─────────────────────────────────────────
            SectionLabel("Workout Type")
            Spacer(Modifier.height(12.dp))
            WorkoutModeGrid(selected = selectedMode, onSelect = { selectedMode = it })

            Spacer(Modifier.height(24.dp))

            // ── Duration picker ───────────────────────────────────────────
            SectionLabel("Duration (min)")
            Spacer(Modifier.height(12.dp))
            DurationPicker(selected = selectedDuration, onSelect = { selectedDuration = it })

            Spacer(Modifier.height(24.dp))

            // ── Intensity ─────────────────────────────────────────────────
            SectionLabel("Intensity")
            Spacer(Modifier.height(12.dp))
            IntensitySelector(selected = selectedIntensity, onSelect = { selectedIntensity = it })

            Spacer(Modifier.height(28.dp))

            // ── Start Now button ──────────────────────────────────────────
            StartNowButton(
                mode = activeMode,
                pulse = pulse,
                isStarting = state.isStarting,
                onStart = { viewModel.startWorkout(selectedMode, selectedDuration, selectedIntensity) }
            )

            // Session started success banner
            if (state.sessionStarted) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(activeMode.gradientColors.map { it.copy(0.20f) }))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡ Session started! ~${state.estimatedCalories} cal estimated",
                        style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Recent ────────────────────────────────────────────────────
            SectionLabel("Recent Quick Starts")
            Spacer(Modifier.height(12.dp))
            RecentList(sessions = state.recentSessions)

            Spacer(Modifier.height(12.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickstartHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Quick Start", style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Pick a mode and go ⚡", style = MaterialTheme.typography.bodySmall,
                color = TextTertiary)
        }
        // Settings / customise
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .liquidGlass(cornerRadius = 22.dp, fillAlpha = 0.12f, borderAlpha = 0.35f)
                .clickable { }
        ) {
            Icon(Icons.Default.Tune, contentDescription = "Settings",
                tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero summary card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroCard(
    mode: WorkoutMode,
    duration: Int,
    intensity: String,
    pulse: Float
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp),
        cornerRadius = 28.dp,
        fillAlpha = 0.14f,
        borderAlpha = 0.45f
    ) {
        // Gradient backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        mode.gradientColors.map { it.copy(0.22f) }
                    )
                )
        )
        // Inner highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Brush.verticalGradient(listOf(Color.White.copy(0.07f), Color.Transparent)))
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large emoji with glow
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    mode.gradientColors.first().copy(0.35f * pulse),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
                Text(mode.emoji, fontSize = 42.sp)
            }

            Spacer(Modifier.width(20.dp))

            Column {
                Text(mode.label,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("⏱ $duration min", mode.gradientColors.first())
                    InfoChip("🔥 $intensity", mode.gradientColors.last())
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(0.18f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall,
            color = color, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Workout mode grid  (2 columns × 3 rows)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WorkoutModeGrid(selected: String, onSelect: (String) -> Unit) {
    val rows = workoutModes.chunked(3)
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { mode ->
                    WorkoutModeTile(
                        mode = mode,
                        selected = selected == mode.id,
                        onClick = { onSelect(mode.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutModeTile(
    mode: WorkoutMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tileScale"
    )

    GlassCard(
        modifier = modifier
            .height(88.dp)
            .scale(scale),
        cornerRadius = 20.dp,
        fillAlpha = if (selected) 0.20f else 0.08f,
        borderAlpha = if (selected) 0.55f else 0.20f,
        onClick = onClick
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(mode.gradientColors.map { it.copy(0.22f) })
                    )
            )
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(mode.emoji, fontSize = 26.sp)
            Spacer(Modifier.height(5.dp))
            Text(
                mode.label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) mode.gradientColors.first() else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        // Selected tick
        if (selected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset((-6).dp, 6.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(mode.gradientColors))
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White,
                    modifier = Modifier.size(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Duration picker
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DurationPicker(selected: Int, onSelect: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(durations) { min ->
            val isSelected = min == selected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.08f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "durScale"
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .size(width = 64.dp, height = 72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isSelected)
                            Brush.verticalGradient(listOf(AccentCyan, AccentPurple))
                        else
                            Brush.verticalGradient(listOf(GlassSurfaceMid, GlassSurface))
                    )
                    .border(
                        if (isSelected) 1.dp else 0.dp,
                        Brush.linearGradient(listOf(AccentCyan.copy(0.6f), AccentPurple.copy(0.3f))),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelect(min) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$min",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "min",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White.copy(0.8f) else TextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Intensity selector
// ─────────────────────────────────────────────────────────────────────────────

private val intensityColors = mapOf(
    "Easy"     to AccentGreen,
    "Moderate" to AccentCyan,
    "Intense"  to WarningAmber,
    "Max"      to AccentPink
)

@Composable
private fun IntensitySelector(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        intensities.forEach { level ->
            val isSelected = level == selected
            val color = intensityColors[level] ?: AccentCyan
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "intScale"
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) color.copy(0.22f)
                        else Brush.linearGradient(listOf(GlassSurface, GlassSurface))
                            .let { GlassSurface }
                            .let { color -> Color.Transparent }
                            .let { GlassSurface }
                    )
                    .background(if (isSelected) color.copy(0.18f) else GlassSurface)
                    .border(
                        1.dp,
                        if (isSelected) color.copy(0.7f) else GlassBorderBot,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelect(level) }
            ) {
                Text(
                    level,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) color else TextTertiary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Start Now button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StartNowButton(mode: WorkoutMode, pulse: Float, isStarting: Boolean = false, onStart: () -> Unit = {}) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    Brush.horizontalGradient(
                        mode.gradientColors.map { it.copy(0.20f * pulse) }
                    ),
                    RoundedCornerShape(36.dp)
                )
        )
        // Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(12.dp, RoundedCornerShape(30.dp),
                    spotColor = mode.gradientColors.first().copy(0.5f))
                .clip(RoundedCornerShape(30.dp))
                .background(Brush.horizontalGradient(mode.gradientColors))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color.White.copy(0.45f), Color.White.copy(0.1f))),
                    RoundedCornerShape(30.dp)
                )
                .clickable { onStart() }
        ) {
            if (isStarting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Bolt, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Text("Start Now", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Recent quick starts
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecentList(sessions: List<RecentSession> = emptyList()) {
    val items: List<Triple<String, String, androidx.compose.ui.graphics.Color>> = if (sessions.isNotEmpty()) {
        sessions.map { s ->
            val name = "${s.mode.replaceFirstChar { it.uppercase() }} Blast"
            val meta = "${s.durationMin} min · ${s.intensity}"
            val color = when (s.mode.lowercase()) {
                "hiit"     -> AccentPink
                "yoga"     -> AccentPurple
                "cardio"   -> AccentGreen
                "cycling"  -> WarningAmber
                "strength" -> AccentCyan
                else       -> AccentCyan
            }
            Triple(name, meta, color)
        }
    } else {
        recentQuickStarts
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (name, meta, color) ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                fillAlpha = 0.08f,
                borderAlpha = 0.22f,
                onClick = {}
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.copy(0.18f))
                    ) {
                        Icon(Icons.Default.Bolt, null, tint = color,
                            modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(meta, style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary)
                    }
                    Icon(Icons.Default.PlayCircleOutline, contentDescription = "Restart",
                        tint = color, modifier = Modifier.size(24.dp).clickable { })
                }
            }
        }
    }
}
