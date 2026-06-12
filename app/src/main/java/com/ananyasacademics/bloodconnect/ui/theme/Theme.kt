package com.ananyasacademics.bloodconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BloodConnectLightColors = lightColorScheme(
    primary = EmergencyRed,
    secondary = TrustBlue,
    tertiary = PreparednessGreen,

    background = NeutralBackground,
    surface = CardBackground,

    onPrimary = CardBackground,
    onSecondary = CardBackground,
    onTertiary = CardBackground,

    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val BloodConnectDarkColors = darkColorScheme(
    primary = EmergencyRed,
    secondary = TrustBlue,
    tertiary = PreparednessGreen
)

@Composable
fun BloodConnectOfflineV6Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BloodConnectLightColors,
        typography = Typography,
        content = content
    )
}