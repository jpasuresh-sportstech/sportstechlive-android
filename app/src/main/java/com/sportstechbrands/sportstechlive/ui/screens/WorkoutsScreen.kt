package com.sportstechbrands.sportstechlive.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportstechbrands.sportstechlive.data.*
import com.sportstechbrands.sportstechlive.ui.components.*
import com.sportstechbrands.sportstechlive.ui.theme.*
import com.sportstechbrands.sportstechlive.ui.viewmodel.WorkoutsViewModel

@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredWorkouts = state.workouts.filter { w ->
        val matchCat    = selectedCategory == null || w.category == selectedCategory
        val matchSearch = searchQuery.isEmpty() || w.title.contains(searchQuery, ignoreCase = true) || w.trainer.contains(searchQuery, ignoreCase = true)
        matchCat && matchSearch
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Workouts", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).liquidGlass(cornerRadius = 22.dp).clickable { }) {
                    Icon(Icons.Default.Tune, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            GlassSearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(14.dp))

            // Category filter chips
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    GlassChip(label = "All", selected = selectedCategory == null, onClick = { selectedCategory = null })
                }
                items(WorkoutRepository.categories) { cat ->
                    GlassChip(
                        label = cat.name,
                        selected = selectedCategory == cat.id,
                        onClick = { selectedCategory = if (selectedCategory == cat.id) null else cat.id }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AccentCyan, strokeWidth = 2.dp)
            } else {
                Text(
                    "${filteredWorkouts.size} workouts",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredWorkouts) { workout -> WorkoutsGridCard(workout = workout) }
                }
            }
        }
    }
}

@Composable
fun GlassSearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(50.dp)
            .liquidGlass(cornerRadius = 25.dp, fillAlpha = 0.10f, borderAlpha = 0.3f)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
            singleLine = true,
            decorationBox = @Composable { inner ->
                Box {
                    if (query.isEmpty()) Text("Search workouts, trainers…", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                    inner()
                }
            },
            modifier = Modifier.weight(1f)
        )
        if (query.isNotEmpty()) {
            Icon(Icons.Default.Close, "Clear", tint = TextTertiary, modifier = Modifier.size(16.dp).clickable { onQueryChange("") })
        }
    }
}

@Composable
private fun WorkoutsGridCard(workout: Workout) {
    GlassCard(modifier = Modifier.fillMaxWidth().height(220.dp), cornerRadius = 22.dp, onClick = {}) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(workout.gradientColors.map { it.copy(0.15f) }, start = Offset.Zero, end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))))
        Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val emoji = WorkoutRepository.categories.find { it.id == workout.category }?.icon ?: "💪"
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(38.dp).liquidGlass(cornerRadius = 12.dp, fillAlpha = 0.15f)) {
                    Text(emoji, fontSize = 18.sp)
                }
                DifficultyBadge(level = workout.difficulty)
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).align(Alignment.CenterHorizontally).liquidGlass(cornerRadius = 22.dp, fillAlpha = 0.2f, borderAlpha = 0.4f).clickable { }) {
                Icon(Icons.Default.PlayArrow, null, tint = AccentCyan, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(workout.title, style = MaterialTheme.typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, lineHeight = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(workout.trainer, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(GlassSurface).padding(horizontal = 6.dp, vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = AccentCyan, modifier = Modifier.size(10.dp))
                        Text("${workout.durationMin}m", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Row(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(GlassSurface).padding(horizontal = 6.dp, vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 9.sp)
                        Text("${workout.calories}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}
