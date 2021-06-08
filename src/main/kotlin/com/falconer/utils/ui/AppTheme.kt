package com.falconer.utils.ui

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

object AppTheme {

    val colors: Colors = Colors()

    class Colors(
        val windowBackground: Color = Color(0xFFEFEFEF),
        val textPrimary: Color = Color(0xFFFFFFFF),
        val textFieldForeground: Color = Color(0xFF0C2031),
        val textFieldBackground: Color = Color(0xFFFFFFFF),


        val material: androidx.compose.material.Colors = darkColors(
            background = windowBackground,
            primary = textPrimary
        ),
    )
}