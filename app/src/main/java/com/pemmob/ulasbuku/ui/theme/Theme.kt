package com.pemmob.ulasbuku.ui.theme

import android.app.Activity
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =========================================================================
// COLOR SCHEME — CUTIE PASTEL POP & PURE WHITE
// Primary  : Vibrant Royal Blue / Dark Charcoal
// Surface  : Pure White
// Background: Pure White
// =========================================================================

private val CuteWhiteColorScheme = lightColorScheme(
    primary = VividBlue,
    onPrimary = Color.White,
    primaryContainer = PastelBlueGradientStart,
    onPrimaryContainer = TextPrimary,

    secondary = PastelPurpleGradientStart,
    onSecondary = TextPrimary,
    secondaryContainer = SoftGray,
    onSecondaryContainer = TextPrimary,

    tertiary = AmberStar,
    onTertiary = Color.White,
    tertiaryContainer = PastelYellowGradientStart,
    onTertiaryContainer = TextPrimary,

    background = PureWhite,
    onBackground = TextPrimary,

    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftGray,
    onSurfaceVariant = TextSecondary,

    outline = BorderDark,
    error = CoralRed,
    onError = Color.White
)

@Composable
fun ulasBukuTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        disabledTextColor = TextMuted,
        errorTextColor = CoralRed,

        focusedContainerColor = SoftGray,
        unfocusedContainerColor = SoftGray,
        disabledContainerColor = SoftGray,
        errorContainerColor = SoftGray,

        focusedBorderColor = TextPrimary,
        unfocusedBorderColor = BorderSubtle,
        disabledBorderColor = BorderSubtle,
        errorBorderColor = CoralRed,

        focusedPlaceholderColor = TextMuted,
        unfocusedPlaceholderColor = TextMuted,
        disabledPlaceholderColor = TextMuted,

        focusedLabelColor = TextPrimary,
        unfocusedLabelColor = TextSecondary,
        disabledLabelColor = TextMuted,
        errorLabelColor = CoralRed,

        focusedLeadingIconColor = VividBlue,
        unfocusedLeadingIconColor = TextMuted,
        disabledLeadingIconColor = TextMuted,
        errorLeadingIconColor = CoralRed,
        focusedTrailingIconColor = TextPrimary,
        unfocusedTrailingIconColor = TextMuted,
        disabledTrailingIconColor = TextMuted,
        errorTrailingIconColor = CoralRed,

        focusedSupportingTextColor = TextSecondary,
        unfocusedSupportingTextColor = TextMuted,
        disabledSupportingTextColor = TextMuted,
        errorSupportingTextColor = CoralRed,

        cursorColor = TextPrimary,
        errorCursorColor = CoralRed,
        selectionColors = TextSelectionColors(
            handleColor = VividBlue,
            backgroundColor = VividBlue.copy(alpha = 0.25f)
        )
    )
}

@Composable
fun UlasBukuTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.White.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    val selectionColors = TextSelectionColors(
        handleColor = VividBlue,
        backgroundColor = VividBlue.copy(alpha = 0.25f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        MaterialTheme(
            colorScheme = CuteWhiteColorScheme,
            typography = Typography,
            content = content
        )
    }
}