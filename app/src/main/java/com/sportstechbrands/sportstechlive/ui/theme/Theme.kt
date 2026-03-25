package com.sportstechbrands.sportstechlive.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SportstechColorScheme = darkColorScheme(
    primary            = AccentCyan,
    onPrimary          = SpaceBlack,
    primaryContainer   = AccentCyan.copy(alpha = 0.15f),
    onPrimaryContainer = AccentCyan,

    secondary          = AccentPurple,
    onSecondary        = TextPrimary,
    secondaryContainer = AccentPurple.copy(alpha = 0.15f),
    onSecondaryContainer = AccentPurple,

    tertiary           = AccentPink,
    onTertiary         = TextPrimary,
    tertiaryContainer  = AccentPink.copy(alpha = 0.15f),
    onTertiaryContainer = AccentPink,

    background         = SpaceBlack,
    onBackground       = TextPrimary,

    surface            = SpaceNavy,
    onSurface          = TextPrimary,
    surfaceVariant     = SpaceDark,
    onSurfaceVariant   = TextSecondary,

    outline            = GlassBorderTop,
    outlineVariant     = GlassBorderBot,

    error              = ErrorRed,
    onError            = Color.White,
)

@Composable
fun SportstechLiveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SportstechColorScheme,
        typography  = SportstechTypography,
        content     = content
    )
}
