package com.jammes.calctmb.domain.model

/**
 * O que o app lembra das respostas ao convite para avaliá-lo na loja. A Google Play nunca
 * conta se a pessoa chegou a avaliar, então tocar em "Avaliar" é o mais perto disso que o
 * app consegue saber.
 */
data class ReviewInviteHistory(
    /** A pessoa tocou em "Avaliar": o convite não volta mais. */
    val accepted: Boolean = false,
    /** Quantas vezes a pessoa deixou o convite para depois. */
    val declineCount: Int = 0,
    /** Quando foi a última recusa, em milissegundos; null se nunca recusou. */
    val lastDeclinedAt: Long? = null
)
