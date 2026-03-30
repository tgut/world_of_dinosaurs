package com.example.world_of_dinosaurs_extented.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DinoLightColorScheme = lightColorScheme(
    primary = DinoGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = DinoGreenDark,
    secondary = DinoBrown,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7CCC8),
    onSecondaryContainer = Color(0xFF3E2723),
    tertiary = DinoAmber,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFF9C4),
    onTertiaryContainer = Color(0xFF5D4037),
    background = DinoSand,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DinoDarkColorScheme = darkColorScheme(
    primary = DinoGreenLight,
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = DinoBrownLight,
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF4E342E),
    onSecondaryContainer = Color(0xFFD7CCC8),
    tertiary = DinoAmberLight,
    onTertiary = Color(0xFF3E2723),
    tertiaryContainer = Color(0xFF5D4037),
    onTertiaryContainer = Color(0xFFFFF9C4),
    background = DinoDarkBg,
    onBackground = Color(0xFFE6E1E5),
    surface = DinoDarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF3C3C3C),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

@Composable
fun DinoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DinoDarkColorScheme else DinoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DinoTypography,
        shapes = DinoShapes,
        content = content
    )
}
