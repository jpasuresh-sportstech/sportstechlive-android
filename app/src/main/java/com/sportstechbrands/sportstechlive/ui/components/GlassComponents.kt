package com.sportstechbrands.sportstechlive.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.sportstechbrands.sportstechlive.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Core Glass Modifier
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Applies the Liquid Glass visual treatment to any composable:
 *   • Semi-transparent white fill (simulated frosted glass)
 *   • Diagonal specular border (bright top-left → dim bottom-right)
 *   • Subtle inner highlight strip at the top
 */
fun Modifier.liquidGlass(
    cornerRadius: Dp = 20.dp,
    fillAlpha: Float = 0.10f,
    borderAlpha: Float = 0.35f,
    elevation: Dp = 8.dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.5f),
            spotColor = Color.Black.copy(alpha = 0.6f)
        )
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                0.0f to Color.White.copy(alpha = fillAlpha + 0.06f),
                0.5f to Color.White.copy(alpha = fillAlpha),
                1.0f to Color.White.copy(alpha = fillAlpha - 0.02f),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                0.0f to Color.White.copy(alpha = borderAlpha),
                0.4f to Color.White.copy(alpha = borderAlpha * 0.5f),
                1.0f to Color.White.copy(alpha = borderAlpha * 0.15f),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ),
            shape = shape
        )
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    fillAlpha: Float = 0.10f,
    borderAlpha: Float = 0.35f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val baseModifier = modifier.liquidGlass(cornerRadius, fillAlpha, borderAlpha)

    if (onClick != null) {
        Box(
            modifier = baseModifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            ),
            content = content
        )
    } else {
        Box(modifier = baseModifier, content = content)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassButton (filled gradient pill)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(AccentCyan, AccentPurple),
    enabled: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.horizontalGradient(
                    if (enabled) gradientColors
                    else listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.2f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.White.copy(0.4f), Color.White.copy(0.1f))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 28.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassChip (category pill)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgBrush = if (selected)
        Brush.horizontalGradient(listOf(AccentCyan, AccentPurple))
    else
        Brush.horizontalGradient(listOf(GlassSurfaceMid, GlassSurface))

    val borderBrush = if (selected)
        Brush.horizontalGradient(listOf(AccentCyan.copy(0.8f), AccentPurple.copy(0.4f)))
    else
        Brush.horizontalGradient(listOf(GlassBorderTop, GlassBorderBot))

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgBrush)
            .border(1.dp, borderBrush, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassBottomNavigation
// ─────────────────────────────────────────────────────────────────────────────

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconSelected: androidx.compose.ui.graphics.vector.ImageVector = icon
)

// The centre index (2 of 5) is the "Start" quick-launch button — rendered as a raised FAB
private const val CENTER_NAV_INDEX = 2

@Composable
fun GlassBottomNavigation(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Infinite glow pulse for the centre FAB
    val infiniteTransition = rememberInfiniteTransition(label = "fabGlow")
    val fabGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "fabGlowF"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(80.dp),              // taller to make room for raised FAB
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Pill bar (sits at bottom) ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter)
                .liquidGlass(cornerRadius = 34.dp, fillAlpha = 0.14f, borderAlpha = 0.4f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (index == CENTER_NAV_INDEX) {
                    // Invisible placeholder so other items space correctly
                    Spacer(Modifier.width(72.dp))
                } else {
                    val selected = currentRoute == item.route
                    GlassNavItem(
                        item = item,
                        selected = selected,
                        onClick = { onItemClick(item.route) }
                    )
                }
            }
        }

        // ── Centre FAB (raised above pill) ───────────────────────────────
        val centreItem = items[CENTER_NAV_INDEX]
        val centreSelected = currentRoute == centreItem.route
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(58.dp)
                // outer glow ring
                .background(
                    Brush.radialGradient(
                        listOf(
                            AccentCyan.copy(alpha = 0.30f * fabGlow),
                            AccentPurple.copy(alpha = 0.15f * fabGlow),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .shadow(16.dp, CircleShape,
                        spotColor = AccentCyan.copy(0.6f),
                        ambientColor = AccentPurple.copy(0.4f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AccentCyan, AccentPurple)
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(Color.White.copy(0.5f), Color.White.copy(0.1f))),
                        CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = Color.White),
                        onClick = { onItemClick(centreItem.route) }
                    )
            ) {
                Icon(
                    imageVector = if (centreSelected) centreItem.iconSelected else centreItem.icon,
                    contentDescription = centreItem.label,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun GlassNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navScale"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, color = AccentCyan),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Brush.radialGradient(listOf(AccentCyan.copy(0.3f), AccentCyan.copy(0f))),
                        CircleShape
                    )
            ) {
                Icon(imageVector = item.iconSelected, contentDescription = item.label,
                    tint = AccentCyan, modifier = Modifier.size(22.dp))
            }
        } else {
            Icon(imageVector = item.icon, contentDescription = item.label,
                tint = TextTertiary, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) AccentCyan else TextDisabled,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassTopBar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp)
            .liquidGlass(cornerRadius = 28.dp, fillAlpha = 0.12f, borderAlpha = 0.3f)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationIcon?.invoke()
        if (navigationIcon != null) Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f)) { title() }
        Row(content = actions)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassStatCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassStatCard(
    value: String,
    label: String,
    accentColor: Color = AccentCyan,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LiveBadge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LiveBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "livePulse"
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LiveRed.copy(alpha = 0.25f))
            .border(1.dp, LiveRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(LiveRed.copy(alpha = pulse), CircleShape)
        )
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall,
            color = LiveRed,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassProgressBar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassProgressBar(
    progress: Float,                 // 0f..1f
    modifier: Modifier = Modifier,
    trackColor: Color = GlassSurfaceMid,
    fillColors: List<Color> = listOf(AccentCyan, AccentPurple),
    height: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(
                    Brush.horizontalGradient(fillColors),
                    RoundedCornerShape(height / 2)
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DifficultyBadge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DifficultyBadge(level: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (level.lowercase()) {
        "beginner"     -> AccentGreen.copy(0.2f) to AccentGreen
        "intermediate" -> WarningAmber.copy(0.2f) to WarningAmber
        "advanced"     -> ErrorRed.copy(0.2f) to ErrorRed
        else           -> GlassSurface to TextTertiary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = level, style = MaterialTheme.typography.labelSmall, color = fg)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassTextField  — frosted-glass input field used across auth screens
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val borderColor = when {
        isError -> ErrorRed.copy(alpha = 0.7f)
        value.isNotEmpty() -> AccentCyan.copy(alpha = 0.5f)
        else -> GlassBorderTop
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(borderColor, borderColor.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (value.isNotEmpty()) AccentCyan else TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(onAny = { onImeAction() }),
                decorationBox = @Composable { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                }
            )

            trailingContent?.invoke()
        }

        // Error message
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = ErrorRed,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassDivider  — "— or —" separator
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassDivider(label: String = "or", modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(GlassBorderTop))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        Box(Modifier.weight(1f).height(1.dp).background(GlassBorderTop))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PasswordStrengthBar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PasswordStrengthBar(password: String, modifier: Modifier = Modifier) {
    val strength = when {
        password.length >= 8 &&
        password.any { it.isUpperCase() } &&
        password.any { it.isDigit() } &&
        password.any { "!@#\$%^&*".contains(it) } -> 4
        password.length >= 8 &&
        password.any { it.isUpperCase() } &&
        password.any { it.isDigit() } -> 3
        password.length >= 6 -> 2
        password.isNotEmpty() -> 1
        else -> 0
    }
    val (label, colors) = when (strength) {
        4 -> "Strong"       to listOf(AccentGreen, AccentCyan)
        3 -> "Good"         to listOf(AccentCyan, AccentPurple)
        2 -> "Fair"         to listOf(WarningAmber, AccentOrange)
        1 -> "Weak"         to listOf(ErrorRed, AccentOrange)
        else -> ""          to listOf(GlassSurface, GlassSurface)
    }

    if (password.isEmpty()) return

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (i < strength)
                                Brush.horizontalGradient(colors)
                            else
                                Brush.horizontalGradient(listOf(GlassSurface, GlassSurface))
                        )
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.first()
        )
    }
}
