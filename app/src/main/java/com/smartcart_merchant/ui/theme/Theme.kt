package com.smartcart_merchant.ui.theme

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

// 🌙 Esquema de colores para Tema Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = SmartCyan,            // El cian resalta excelente en fondo oscuro
    secondary = SmartBlue,
    tertiary = SmartGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,        // Texto negro sobre botones cian para legibilidad
    onSecondary = Color.White,
    onTertiary = Color.Black
)

// ☀️ Esquema de colores para Tema Claro
private val LightColorScheme = lightColorScheme(
    primary = SmartBlue,            // El azul funciona genial como color predominante en claro
    secondary = SmartCyan,          // Para componentes como los botones redondeados de tu UI
    tertiary = SmartGreen,          // Para acentos, contadores o detalles específicos
    background = LightBackground,   // Fondo grisáceo muy tenue
    surface = LightSurface,         // Tarjetas blancas como el bloque "Información del Proyecto"
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun SmartcartmerchantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Tip: Si quieres mantener estrictamente TU paleta y que Android no la cambie
    // dinámicamente con el fondo de pantalla del usuario, puedes poner esto en 'false'.
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