package com.example.gamehub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

public val BlueGH = Color(0xFF0D47A1)
public val RedGH = Color(0xFFD32F2F)
public val BlackGH = Color(0xFF121212)
public val SurfaceDarkGH = Color(0xFF1E1E1E)

//COLORES PERSONALIZADOS
val HighlightGH = Color(0xFF7825BE)
val FavoriteGH = Color(0xFFFFC107)

private val GameHubDarkColors = darkColorScheme(
    primary = BlueGH,
    onPrimary = Color.White,
    primaryContainer = BlueGH,

    secondary = RedGH,
    onSecondary = Color.White,
    secondaryContainer = RedGH,

    tertiary = HighlightGH,
    onTertiary = Color.Black,
    tertiaryContainer = HighlightGH,

    background = BlackGH,
    onBackground = Color.White,

    surface = SurfaceDarkGH,
    onSurface = Color.White
)

@Composable
fun GameHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = GameHubDarkColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}