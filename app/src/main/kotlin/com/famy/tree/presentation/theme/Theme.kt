package com.famy.tree.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.famy.tree.data.model.ThemePreference

private val LightScheme = lightColorScheme(
    primary = FamySeed,
    onPrimary = Color.White,
    secondary = FamyLeaf,
    tertiary = FamyEarth,
    background = Color(0xFFF8FBF8),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE7EFEB)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF9ED5C0),
    onPrimary = Color(0xFF06382F),
    secondary = Color(0xFFB7CCBE),
    tertiary = Color(0xFFD9C3A1),
    background = Color(0xFF0F1513),
    surface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFF3F4945)
)

@Composable
fun FamyTheme(
    preference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dark = when (preference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context is Activity) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (dark) DarkScheme else LightScheme

    MaterialTheme(
        colorScheme = colors,
        typography = FamyTypography,
        content = content
    )
}
