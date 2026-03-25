package com.sportstechbrands.sportstechlive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*

@Composable
fun LiveScreen() {
    val liveNow = WorkoutRepository.liveSessions.first { it.isLiveNow }
    val upcoming = WorkoutRepository.liveSessions.filter { !it.isLiveNow }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                LiveScreenHeader()
                Spacer(Modifier.height(16.dp))
            }

            // ── Currently Live ─────────────────────────────────────────────
            item {
                Text(
                    text = "On Air Now",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
                LiveNowCard(session = liveNow)
                Spacer(Modifier.height(24.dp))
            }

            // ── Upcoming ───────────────────────────────────────────────────
            item {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            items(upcoming) { session ->
                UpcomingSessionCard(session = session)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LiveScreenHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Live & Schedule",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Join expert-led sessions in real time",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        LiveBadge()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Live Now Hero Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LiveNowCard(session: LiveSession) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "glow"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 16.dp),
        cornerRadius = 28.dp,
        fillAlpha = 0.14f,
        borderAlpha = 0.45f,
        onClick = {}
    ) {
        // Pulsing live glow background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LiveRed.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Gradient backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = session.gradientColors.map { it.copy(alpha = 0.2f) },
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiveBadge()
                // Viewer count
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(Icons.Default.Visibility, null, tint = TextTertiary, modifier = Modifier.size(12.dp))
                    Text(
                        "${session.participants} watching",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Big play button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        Brush.radialGradient(
                            listOf(AccentCyan.copy(0.2f), AccentCyan.copy(0f))
                        ),
                        CircleShape
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .liquidGlass(cornerRadius = 28.dp, fillAlpha = 0.25f, borderAlpha = 0.5f)
                        .clickable { }
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        tint = AccentCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Trainer avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    Brush.linearGradient(listOf(AccentCyan, AccentPurple)),
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = session.trainer.first().toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = session.trainer,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Timer, null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                        Text(
                            "${session.durationMin} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Upcoming Session Row Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingSessionCard(session: LiveSession) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 20.dp,
        onClick = {}
    ) {
        // Side accent stripe
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(session.gradientColors),
                    RoundedCornerShape(2.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp)
            ) {
                Text(
                    text = session.startTime,
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.durationMin}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(GlassBorderTop)
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = session.trainer,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(TextDisabled, CircleShape)
                    )
                    Text(
                        text = session.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            // Set reminder button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .liquidGlass(cornerRadius = 18.dp, fillAlpha = 0.12f)
                    .clickable { }
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
