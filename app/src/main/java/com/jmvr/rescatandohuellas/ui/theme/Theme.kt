package com.jmvr.rescatandohuellas.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = HuellaOrange,
    onPrimary = Color.White,
    secondary = HuellaGreen,
    onSecondary = Color.White,
    tertiary = HuellaPurple,
    onTertiary = Color.White,
    background = HuellaBackground,
    onBackground = HuellaOnBackground,
    surface = HuellaSurface,
    onSurface = HuellaOnBackground,
    error = HuellaCoral,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = HuellaOrange,
    secondary = HuellaGreen,
    tertiary = HuellaPurple,
    error = HuellaCoral
)

@Composable
fun RescatandoHuellasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // El color de marca es parte de la identidad del producto: no lo
    // pisamos con el color dinámico del wallpaper del usuario (Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
