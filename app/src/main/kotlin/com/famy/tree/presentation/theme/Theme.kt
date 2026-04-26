package com.famy.tree.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.ThemePreference

private val DarkScheme = darkColorScheme(
    primary = FamyPrimary,
    onPrimary = Color(0xFF052112),
    primaryContainer = FamyPrimarySoft,
    onPrimaryContainer = Color(0xFFBFF1CF),
    secondary = FamySecondary,
    onSecondary = Color(0xFF0D1B34),
    secondaryContainer = FamySecondarySoft,
    onSecondaryContainer = Color(0xFFD4E4FF),
    tertiary = FamyAccent,
    onTertiary = Color(0xFF261807),
    tertiaryContainer = FamyAccentSoft,
    onTertiaryContainer = Color(0xFFFFE0B5),
    background = FamyBackground,
    onBackground = Color(0xFFF4F6F5),
    surface = FamySurface,
    onSurface = Color(0xFFF4F6F5),
    surfaceVariant = FamySurfaceSoft,
    onSurfaceVariant = FamyTextSoft,
    outline = FamyOutline,
    outlineVariant = Color(0xFF242A2B),
    error = Color(0xFFFF8A8A),
    errorContainer = Color(0xFF3B1718)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF1E7A49),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F7E2),
    onPrimaryContainer = Color(0xFF052112),
    secondary = Color(0xFF39669D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E7FF),
    onSecondaryContainer = Color(0xFF0E1D31),
    tertiary = Color(0xFF9B6A22),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDEA9),
    onTertiaryContainer = Color(0xFF2E1600),
    background = Color(0xFFF6F8F7),
    onBackground = Color(0xFF131515),
    surface = Color.White,
    onSurface = Color(0xFF131515),
    surfaceVariant = Color(0xFFECEFED),
    onSurfaceVariant = Color(0xFF53605C),
    outline = Color(0xFFD6DBD9),
    outlineVariant = Color(0xFFE1E5E3),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC)
)

private val FamyShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun FamyTheme(
    preference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val colors = when (preference) {
        ThemePreference.LIGHT -> LightScheme
        ThemePreference.DARK -> DarkScheme
        ThemePreference.SYSTEM -> DarkScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = FamyTypography,
        shapes = FamyShapes,
        content = content
    )
}
