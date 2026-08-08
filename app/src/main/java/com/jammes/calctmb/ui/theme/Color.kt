package com.jammes.calctmb.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Paleta da marca. Toda cor do app sai daqui — não há cor dinâmica do sistema,
 * para que a identidade visual seja a mesma em qualquer aparelho.
 */
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Platinum = Color(0xFFE5E5E5)
val OxfordBlue = Color(0xFF14213D)
val OrangeWeb = Color(0xFFFCA311)

/*
 * Tons derivados das cinco cores acima. O Material 3 exige papéis intermediários
 * (containers, contornos, níveis de superfície) que nenhuma paleta de cinco
 * cores cobre sozinha; todos aqui são clareamentos/escurecimentos das bases.
 */

// Derivados do Oxford Blue
val OxfordBlueDeep = Color(0xFF0C1426)
val OxfordBlueSoft = Color(0xFF1E2C4C)
val OxfordBlueElevated = Color(0xFF25355A)
val OxfordBlueMuted = Color(0xFF4A5568)
val OxfordBlueTint = Color(0xFFDCE1EC)
val OxfordBluePale = Color(0xFFC3CDE4)

// Derivados do Orange Web
val AmberContainerLight = Color(0xFFFFE8C2)
val AmberOnContainerLight = Color(0xFF3D2600)
val AmberContainerDark = Color(0xFF4A3000)
val AmberOnContainerDark = Color(0xFFFFDDA6)

// Neutros claros (entre White e Platinum)
val NeutralLowest = Color(0xFFFFFFFF)
val NeutralLow = Color(0xFFFAFAFB)
val Neutral = Color(0xFFF4F5F7)
val NeutralHigh = Color(0xFFEDEEF1)
val NeutralOutline = Color(0xFFB9BDC7)

// Neutros escuros (entre Black e Oxford Blue)
val NightLow = Color(0xFF080B12)
val Night = Color(0xFF0D131F)
val NightOutline = Color(0xFF3A4763)

// Erro: fora da paleta por necessidade — vermelho é sinal de risco e não deve
// competir com o âmbar de ação.
val ErrorLight = Color(0xFFB3261E)
val ErrorContainerLight = Color(0xFFF9DEDC)
val OnErrorContainerLight = Color(0xFF410E0B)
val ErrorDark = Color(0xFFF2B8B5)
val ErrorContainerDark = Color(0xFF8C1D18)
val OnErrorContainerDark = Color(0xFFF9DEDC)

/**
 * Superfícies de marca: mantêm a mesma cor nos dois temas por carregarem a
 * identidade do app. É o que garante contraste constante para a logo branca,
 * independentemente de o aparelho estar em modo claro ou escuro.
 */
object BrandSurface {
    val Navy = OxfordBlue
    val NavyElevated = OxfordBlueSoft
    val OnNavy = White
    val OnNavyMuted = Color(0xFF9FAAC4)
    val Accent = OrangeWeb
    val OnAccent = Black
}
