package com.jammes.calctmb.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Default = Typography()

/**
 * Escala padrão do Material com dois ajustes: números grandes ganham peso e
 * espaçamento negativo (leitura mais compacta e sólida), e rótulos de seção
 * ganham espaçamento positivo, que é o que dá o ar editorial.
 */
val AppTypography = Typography(
    displayMedium = Default.displayMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-1).sp
    ),
    displaySmall = Default.displaySmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = Default.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = Default.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Default.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Default.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    labelMedium = Default.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )
)
