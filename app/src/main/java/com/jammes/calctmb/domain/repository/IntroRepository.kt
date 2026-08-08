package com.jammes.calctmb.domain.repository

/**
 * Controla a exibição única do diálogo explicativo sobre a TMB.
 */
interface IntroRepository {
    suspend fun wasIntroShown(): Boolean
    suspend fun markIntroAsShown()
}
