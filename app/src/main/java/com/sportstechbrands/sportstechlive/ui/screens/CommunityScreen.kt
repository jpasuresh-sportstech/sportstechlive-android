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
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportstechbrands.sportstechlive.data.network.ApiChallenge
import com.sportstechbrands.sportstechlive.data.network.ApiLeaderEntry
import com.sportstechbrands.sportstechlive.data.network.toComposeColor
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.CommunityViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Data models
// ─────────────────────────────────────────────────────────────────────────────

private data class CommunityPost(
    val id: Int,
    val userName: String,
    val initials: String,
    val avatarColors: List<Color>,
    val timeAgo: String,
    val activity: String,
    val stats: String,
    val emoji: String,
    val accentColor: Color,
    val likes: Int,
    val comments: Int,
    val hasImage: Boolean = false
)

private data class ChallengeCard(
    val title: String,
    val description: String,
    val participants: Int,
    val daysLeft: Int,
    val progress: Float,
    val gradientColors: List<Color>,
    val emoji: String
)

private data class LeaderEntry(
    val rank: Int,
    val name: String,
    val initials: String,
    val avatarColors: List<Color>,
    val points: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false
)

private val samplePosts = listOf(
    CommunityPost(1, "Jordan Kim", "JK", listOf(AccentCyan, AccentPurple),
        "2m ago", "Crushed a 45-min HIIT session 🔥", "542 cal · 8.2 km",
        "🏃", AccentCyan, 48, 12),
    CommunityPost(2, "Maya Rodriguez", "MR", listOf(AccentPink, AccentOrange),
        "18m ago", "New personal best on bench press!", "85 kg · 5×5 sets",
        "💪", AccentPink, 124, 31),
    CommunityPost(3, "Sam Chen", "SC", listOf(AccentGreen, AccentCyan),
        "1h ago", "Morning yoga flow to start the week ✨", "30 min · Level 2",
        "🧘", AccentGreen, 87, 19),
    CommunityPost(4, "Alex Torres", "AT", listOf(AccentPurple, AccentPink),
        "3h ago", "Completed 30-Day Strength Challenge!", "Day 30/30",
        "🏆", AccentPurple, 203, 44),
    CommunityPost(5, "Riley Patel", "RP", listOf(WarningAmber, AccentOrange),
        "5h ago", "Cycling along the coast — beautiful sunrise ride", "32 km · 1h 20m",
        "🚴", WarningAmber, 156, 27)
)

private val sampleChallenges = listOf(
    ChallengeCard("30-Day Shred", "Daily HIIT + strength combo", 2841, 12,
        0.60f, listOf(AccentCyan, AccentPurple), "🔥"),
    ChallengeCard("Run 100K", "Track every outdoor run this month", 1432, 8,
        0.35f, listOf(AccentGreen, AccentCyan), "🏃"),
    ChallengeCard("Zen Week", "7 days of mindfulness & yoga", 893, 3,
        0.85f, listOf(AccentPurple, AccentPink), "🧘")
)

private val leaderboard = listOf(
    LeaderEntry(1, "Jordan Kim", "JK", listOf(AccentCyan, AccentPurple), 9840, 28),
    LeaderEntry(2, "Maya Rodriguez", "MR", listOf(AccentPink, AccentOrange), 8720, 21),
    LeaderEntry(3, "Alex Weber", "AW", listOf(AccentCyan, AccentGreen), 7650, 18, isCurrentUser = true),
    LeaderEntry(4, "Sam Chen", "SC", listOf(AccentGreen, AccentCyan), 6920, 15),
    LeaderEntry(5, "Riley Patel", "RP", listOf(WarningAmber, AccentOrange), 6100, 12)
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CommunityScreen(viewModel: CommunityViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Feed", "Challenges", "Leaderboard")

    // Map API data to local display models (fallback to static if empty)
    val displayLeaders: List<LeaderEntry> = if (state.leaderboard.isNotEmpty()) {
        state.leaderboard.map { api ->
            val initials = api.name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
            val colors = listOf(AccentCyan, AccentPurple)
            LeaderEntry(api.rank, api.name, initials, colors, api.points, api.streak, api.isCurrentUser)
        }
    } else leaderboard

    val displayChallenges: List<ChallengeCard> = if (state.challenges.isNotEmpty()) {
        state.challenges.map { api ->
            ChallengeCard(
                title = api.title,
                description = api.description,
                participants = api.participants.size,
                daysLeft = 7,
                progress = 0.5f,
                gradientColors = api.gradientColors.map { it.toComposeColor() },
                emoji = api.emoji
            )
        }
    } else sampleChallenges

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        ) {
            CommunityTopBar()
            CommunityTabRow(tabs = tabs, selected = selectedTab, onSelect = { selectedTab = it })
            Spacer(Modifier.height(4.dp))
            when (selectedTab) {
                0 -> FeedTab()
                1 -> ChallengesTab(challenges = displayChallenges)
                2 -> LeaderboardTab(leaders = displayLeaders)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommunityTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Community",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "2,841 members active today",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        // Search button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .liquidGlass(cornerRadius = 22.dp, fillAlpha = 0.12f, borderAlpha = 0.35f)
                .clickable { }
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search",
                tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        // Compose button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(AccentCyan, AccentPurple)))
                .clickable { }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Post",
                tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommunityTabRow(
    tabs: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { i, tab ->
            val isSelected = i == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(
                        if (isSelected) Brush.horizontalGradient(listOf(AccentCyan, AccentPurple))
                        else Brush.horizontalGradient(listOf(GlassSurface, GlassSurface))
                    )
                    .border(
                        1.dp,
                        if (isSelected) AccentCyan.copy(0.5f) else GlassBorderBot,
                        RoundedCornerShape(19.dp)
                    )
                    .clickable { onSelect(i) }
            ) {
                Text(
                    text = tab,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else TextTertiary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Feed Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FeedTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Active friends story row
        Spacer(Modifier.height(8.dp))
        Text(
            "Active Now",
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(samplePosts) { post ->
                ActiveFriendBubble(post)
            }
        }
        Spacer(Modifier.height(20.dp))

        // Posts
        Text(
            "Recent Activity",
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(10.dp))
        samplePosts.forEach { post ->
            PostCard(post)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ActiveFriendBubble(post: CommunityPost) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Active ring
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(post.avatarColors),
                        shape = CircleShape
                    )
            )
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(post.avatarColors))
            ) {
                Text(post.initials, style = MaterialTheme.typography.labelMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
            }
            // Green active dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF1A1A2E), CircleShape)
                    .align(Alignment.BottomEnd)
                    .padding(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AccentGreen, CircleShape)
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(
            post.userName.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PostCard(post: CommunityPost) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 20.dp,
        fillAlpha = 0.10f,
        borderAlpha = 0.3f
    ) {
        // Subtle gradient tint from accent color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(post.accentColor.copy(0.04f), Color.Transparent))
                )
        )
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(post.avatarColors))
                ) {
                    Text(post.initials, style = MaterialTheme.typography.labelMedium,
                        color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(post.timeAgo, style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary)
                }
                Icon(Icons.Default.MoreVert, contentDescription = null,
                    tint = TextDisabled, modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Activity pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(post.accentColor.copy(0.10f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(post.emoji, fontSize = 22.sp)
                Column {
                    Text(post.activity, style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary, fontWeight = FontWeight.Medium)
                    Text(post.stats, style = MaterialTheme.typography.labelSmall,
                        color = post.accentColor)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.clickable { }
                ) {
                    Icon(Icons.Default.FavoriteBorder, null,
                        tint = TextTertiary, modifier = Modifier.size(16.dp))
                    Text(post.likes.toString(), style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.clickable { }
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, null,
                        tint = TextTertiary, modifier = Modifier.size(16.dp))
                    Text(post.comments.toString(), style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Share, null,
                    tint = TextDisabled, modifier = Modifier.size(16.dp).clickable { })
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Challenges Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChallengesTab(challenges: List<ChallengeCard> = sampleChallenges) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Your active challenge highlight
        YourChallengeCard()

        Spacer(Modifier.height(20.dp))
        Text("Discover Challenges", style = MaterialTheme.typography.titleSmall,
            color = TextSecondary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))

        challenges.forEach { challenge ->
            ChallengeItemCard(challenge)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun YourChallengeCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        fillAlpha = 0.13f,
        borderAlpha = 0.4f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(AccentCyan.copy(0.08f), AccentPurple.copy(0.12f))))
        )
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("30-Day Shred", style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Your active challenge", style = MaterialTheme.typography.bodySmall,
                        color = AccentCyan)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen.copy(0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Day 18", style = MaterialTheme.typography.labelSmall,
                        color = AccentGreen, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text("60%", style = MaterialTheme.typography.labelSmall,
                    color = AccentCyan, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            GlassProgressBar(progress = 0.60f, height = 10.dp)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                ChallengeStatMini("12 days", "remaining")
                ChallengeStatMini("2,841", "participants")
                ChallengeStatMini("#3", "your rank")
            }
        }
    }
}

@Composable
private fun ChallengeStatMini(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
    }
}

@Composable
private fun ChallengeItemCard(challenge: ChallengeCard) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        fillAlpha = 0.08f,
        borderAlpha = 0.25f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(challenge.gradientColors.map { it.copy(0.06f) })
                )
        )
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(challenge.gradientColors.map { it.copy(0.2f) }))
            ) {
                Text(challenge.emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(challenge.title, style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(challenge.description, style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary)
                Spacer(Modifier.height(8.dp))
                GlassProgressBar(
                    progress = challenge.progress,
                    fillColors = challenge.gradientColors,
                    height = 6.dp
                )
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("👥 ${challenge.participants}", style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary)
                    Text("⏳ ${challenge.daysLeft}d left", style = MaterialTheme.typography.labelSmall,
                        color = challenge.gradientColors.first())
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(challenge.gradientColors))
                    .clickable { }
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Leaderboard Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LeaderboardTab(leaders: List<LeaderEntry> = leaderboard) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))

        // Top 3 podium
        PodiumSection(leaders)

        Spacer(Modifier.height(20.dp))

        // Full list
        Text("This Week's Rankings", style = MaterialTheme.typography.titleSmall,
            color = TextSecondary, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(10.dp))

        leaders.forEach { entry ->
            LeaderRow(entry)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PodiumSection(leaders: List<LeaderEntry> = leaderboard) {
    val top3 = leaders.take(3)
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 24.dp,
        fillAlpha = 0.10f,
        borderAlpha = 0.3f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(WarningAmber.copy(0.06f), Color.Transparent)
                ))
        )
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆 Weekly Top Performers", style = MaterialTheme.typography.titleSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            // Podium: 2nd | 1st | 3rd
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                if (top3.size > 1) PodiumItem(top3[1], 80.dp)
                if (top3.isNotEmpty()) PodiumItem(top3[0], 100.dp)
                if (top3.size > 2) PodiumItem(top3[2], 65.dp)
            }
        }
    }
}

@Composable
private fun PodiumItem(entry: LeaderEntry, height: Dp) {
    val medal = when (entry.rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(medal, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (entry.rank == 1) 56.dp else 46.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(entry.avatarColors))
                .border(
                    if (entry.rank == 1) 2.dp else 0.dp,
                    Brush.linearGradient(listOf(WarningAmber, AccentOrange)),
                    CircleShape
                )
        ) {
            Text(entry.initials, style = MaterialTheme.typography.labelMedium,
                color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(entry.name.split(" ").first(), style = MaterialTheme.typography.labelSmall,
            color = if (entry.rank == 1) WarningAmber else TextSecondary,
            fontWeight = if (entry.rank == 1) FontWeight.Bold else FontWeight.Normal)
        Text("${entry.points} pts", style = MaterialTheme.typography.labelSmall,
            color = TextTertiary, fontSize = 10.sp)

        Spacer(Modifier.height(8.dp))

        // Podium block
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        if (entry.rank == 1) listOf(WarningAmber.copy(0.5f), WarningAmber.copy(0.2f))
                        else listOf(GlassSurfaceHi, GlassSurface)
                    )
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "#${entry.rank}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (entry.rank == 1) WarningAmber else TextTertiary,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun LeaderRow(entry: LeaderEntry) {
    val isCurrentUser = entry.isCurrentUser
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 16.dp,
        fillAlpha = if (isCurrentUser) 0.18f else 0.08f,
        borderAlpha = if (isCurrentUser) 0.5f else 0.25f
    ) {
        if (isCurrentUser) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(AccentCyan.copy(0.08f), AccentPurple.copy(0.05f))))
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                "#${entry.rank}",
                style = MaterialTheme.typography.titleSmall,
                color = when (entry.rank) {
                    1 -> WarningAmber; 2 -> Color(0xFFB0BEC5); 3 -> AccentOrange
                    else -> TextTertiary
                },
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(32.dp)
            )
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(entry.avatarColors))
                    .then(
                        if (isCurrentUser) Modifier.border(2.dp,
                            Brush.linearGradient(listOf(AccentCyan, AccentPurple)), CircleShape)
                        else Modifier
                    )
            ) {
                Text(entry.initials, style = MaterialTheme.typography.labelMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.name, style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentCyan.copy(0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("You", style = MaterialTheme.typography.labelSmall,
                                color = AccentCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("🔥 ${entry.streak}-day streak", style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary)
            }
            Text(
                "${entry.points} pts",
                style = MaterialTheme.typography.titleSmall,
                color = if (isCurrentUser) AccentCyan else TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
