package com.example.gamehub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.gamehub.model.AppTheme

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
    secondary = RedGH,
    tertiary = HighlightGH,
    background = BlackGH,
    surface = SurfaceDarkGH
)

private val GameHubLightColors = lightColorScheme(
    primary = BlueGH,
    onPrimary = Color.White,
    secondary = RedGH,
    tertiary = HighlightGH,
    background = Color.White,
    surface = Color(0xFFF5F5F5)
)

@Composable
fun GameHubTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (darkTheme) GameHubDarkColors else GameHubLightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
