package com.jammes.calctmb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * Âmbar é a cor de ação (primary) e navy é a cor de estrutura (secondary).
 * Essa divisão se mantém nos dois temas, então um botão nunca troca de papel
 * quando o usuário alterna claro/escuro.
 */

private val LightColors = lightColorScheme(
    primary = OrangeWeb,
    onPrimary = Black,
    primaryContainer = AmberContainerLight,
    onPrimaryContainer = AmberOnContainerLight,

    secondary = OxfordBlue,
    onSecondary = White,
    secondaryContainer = OxfordBlueTint,
    onSecondaryContainer = OxfordBlue,

    tertiary = OxfordBlueMuted,
    onTertiary = White,
    tertiaryContainer = Platinum,
    onTertiaryContainer = OxfordBlue,

    error = ErrorLight,
    onError = White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    background = White,
    onBackground = OxfordBlue,
    surface = White,
    onSurface = OxfordBlue,
    surfaceVariant = Platinum,
    onSurfaceVariant = OxfordBlueMuted,

    outline = NeutralOutline,
    outlineVariant = Platinum,
    scrim = Black,

    inverseSurface = OxfordBlue,
    inverseOnSurface = White,
    inversePrimary = OrangeWeb,

    surfaceDim = Platinum,
    surfaceBright = White,
    surfaceContainerLowest = NeutralLowest,
    surfaceContainerLow = NeutralLow,
    surfaceContainer = Neutral,
    surfaceContainerHigh = NeutralHigh,
    surfaceContainerHighest = Platinum
)

private val DarkColors = darkColorScheme(
    primary = OrangeWeb,
    onPrimary = Black,
    primaryContainer = AmberContainerDark,
    onPrimaryContainer = AmberOnContainerDark,

    secondary = OxfordBluePale,
    onSecondary = OxfordBlue,
    secondaryContainer = OxfordBlueSoft,
    onSecondaryContainer = OxfordBlueTint,

    tertiary = OxfordBluePale,
    onTertiary = OxfordBlueDeep,
    tertiaryContainer = OxfordBlueSoft,
    onTertiaryContainer = Platinum,

    error = ErrorDark,
    onError = Black,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    surfaceVariant = OxfordBlueSoft,
    onSurfaceVariant = OxfordBluePale,

    outline = NightOutline,
    outlineVariant = OxfordBlueSoft,
    scrim = Black,

    inverseSurface = Platinum,
    inverseOnSurface = OxfordBlue,
    inversePrimary = OxfordBlue,

    surfaceDim = Black,
    surfaceBright = OxfordBlueElevated,
    surfaceContainerLowest = Black,
    surfaceContainerLow = NightLow,
    surfaceContainer = Night,
    surfaceContainerHigh = OxfordBlue,
    surfaceContainerHighest = OxfordBlueSoft
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
