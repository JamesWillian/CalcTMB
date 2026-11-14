package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.ReviewInviteHistory
import com.jammes.calctmb.domain.repository.ReviewInviteRepository
import com.jammes.calctmb.domain.repository.StoreReview
import com.jammes.calctmb.domain.util.TimeProvider
import java.util.concurrent.TimeUnit

/**
 * Quantos dias o convite espera depois de cada recusa: a segunda chance vem logo, e as
 * seguintes cada vez mais espaçadas, para quem já disse "agora não" algumas vezes não se
 * sentir cobrado. Da terceira recusa em diante, vale o último valor.
 */
internal val DAYS_TO_WAIT_AFTER_DECLINE = listOf(7L, 30L, 90L)

/** Se o convite pode aparecer agora, dado o que a pessoa já respondeu antes. */
internal fun canInviteToReview(history: ReviewInviteHistory, now: Long): Boolean {
    if (history.accepted) return false
    val lastDeclinedAt = history.lastDeclinedAt ?: return true
    val waitDays = DAYS_TO_WAIT_AFTER_DECLINE[
        (history.declineCount - 1).coerceIn(0, DAYS_TO_WAIT_AFTER_DECLINE.lastIndex)
    ]
    // Com o relógio atrasado (now antes da recusa), a espera continua valendo.
    return now >= lastDeclinedAt + TimeUnit.DAYS.toMillis(waitDays)
}

class ShouldInviteToReviewUseCase(
    private val repository: ReviewInviteRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(): Boolean =
        canInviteToReview(repository.history(), timeProvider.currentTimeMillis())
}

/**
 * A pessoa aceitou: o convite não volta mais, mesmo que ela feche o cartão sem avaliar, já que
 * a loja não conta se a avaliação aconteceu. A resposta é guardada antes de abrir a loja, para
 * valer mesmo se o app for fechado com o cartão aberto.
 */
class AcceptReviewInviteUseCase(
    private val repository: ReviewInviteRepository,
    private val storeReview: StoreReview
) {
    suspend operator fun invoke() {
        repository.markAccepted()
        storeReview.open()
    }
}

class DeclineReviewInviteUseCase(
    private val repository: ReviewInviteRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke() = repository.markDeclined(timeProvider.currentTimeMillis())
}
