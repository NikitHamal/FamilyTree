package com.sikai.learn.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = CosmicIndigo,
    onPrimary = Color.White,
    primaryContainer = CosmicIndigoLight,
    onPrimaryContainer = Color.White,
    secondary = VedicGold,
    onSecondary = CosmicIndigoDark,
    secondaryContainer = PressedPaper,
    onSecondaryContainer = CosmicIndigo,
    tertiary = MarsRed,
    onTertiary = Color.White,
    background = Vellum,
    onBackground = CosmicIndigo,
    surface = PressedPaper,
    onSurface = CosmicIndigo,
    surfaceVariant = PaperHover,
    onSurfaceVariant = CosmicIndigoLight,
    outline = BorderSubtle,
    outlineVariant = BorderStrong,
    error = MarsRed,
    onError = Color.White,
    errorContainer = MarsRed.copy(alpha = 0.12f),
    onErrorContainer = MarsRed
)

private val DarkColors = darkColorScheme(
    primary = DarkGoldAccent,
    onPrimary = CosmicIndigoDark,
    primaryContainer = DarkPaperHover,
    onPrimaryContainer = DarkGoldAccent,
    secondary = DarkGoldAccent,
    onSecondary = CosmicIndigoDark,
    secondaryContainer = DarkPaper,
    onSecondaryContainer = DarkGoldAccent,
    tertiary = MarsRed,
    onTertiary = Color.White,
    background = DarkVellum,
    onBackground = Color.White,
    surface = DarkPaper,
    onSurface = Color.White,
    surfaceVariant = DarkPaperHover,
    onSurfaceVariant = DarkSlateMuted,
    outline = DarkBorderSubtle,
    outlineVariant = DarkBorderStrong,
    error = MarsRed,
    onError = Color.White,
    errorContainer = MarsRed.copy(alpha = 0.12f),
    onErrorContainer = MarsRed
)

@Composable
fun NeoVedicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NeoVedicTypography,
        content = content
    )
}
