package com.example.asinahaberuygulamasi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BundleBlue,
    secondary = BundleSelected,
    tertiary = BundleRed,
    background = BundleDarkBg,
    surface = BundleSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = BundleTextPrimary,
    onSurface = BundleTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = BundleBlue,
    secondary = Color.Gray,
    tertiary = BundleRed,
    background = Color.White,
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun AsinaHaberUygulamasiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
