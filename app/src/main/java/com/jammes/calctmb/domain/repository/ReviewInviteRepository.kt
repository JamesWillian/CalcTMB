package com.jammes.calctmb.domain.repository

import com.jammes.calctmb.domain.model.ReviewInviteHistory

/**
 * Guarda as respostas ao convite para avaliar o app, para ele não voltar para quem já
 * aceitou nem aparecer logo depois de uma recusa.
 */
interface ReviewInviteRepository {
    suspend fun history(): ReviewInviteHistory
    suspend fun markAccepted()
    suspend fun markDeclined(atMillis: Long)
}
