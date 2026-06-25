package com.mantapp.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MantappEmerald,
    onPrimary = MantappMint,
    primaryContainer = MantappMint,
    onPrimaryContainer = MantappIndigo,
    secondary = MantappGold,
    onSecondary = MantappIndigo,
    tertiary = MantappCoral,
    background = MantappSurface,
    onBackground = MantappInk,
    surface = MantappSurface,
    onSurface = MantappInk,
)

@Composable
fun MantappTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = MantappTypography,
        content = content,
    )
}
