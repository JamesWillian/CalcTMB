package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.ReviewInviteHistory
import com.jammes.calctmb.domain.repository.ReviewInviteRepository
import com.jammes.calctmb.domain.repository.StoreReview
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

/** Quando o convite para avaliar o app aparece, e o que o app guarda das respostas. */
class ReviewInviteUseCasesTest {

    @Test
    fun `quem nunca respondeu recebe o convite`() {
        assertTrue(canInviteToReview(ReviewInviteHistory(), now = NOW))
    }

    @Test
    fun `quem aceitou nunca mais recebe o convite`() {
        val accepted = ReviewInviteHistory(accepted = true)

        assertFalse(canInviteToReview(accepted, now = NOW))
        assertFalse(canInviteToReview(accepted, now = NOW + days(3650)))
    }

    @Test
    fun `depois da primeira recusa o convite espera 7 dias`() {
        val history = declined(times = 1)

        assertFalse(canInviteToReview(history, now = NOW + days(7) - 1))
        assertTrue(canInviteToReview(history, now = NOW + days(7)))
    }

    @Test
    fun `depois da segunda recusa o convite espera 30 dias`() {
        val history = declined(times = 2)

        assertFalse(canInviteToReview(history, now = NOW + days(29)))
        assertTrue(canInviteToReview(history, now = NOW + days(30)))
    }

    @Test
    fun `da terceira recusa em diante o convite espera 90 dias`() {
        listOf(3, 4, 10).forEach { times ->
            val history = declined(times)

            assertFalse("$times recusas", canInviteToReview(history, now = NOW + days(89)))
            assertTrue("$times recusas", canInviteToReview(history, now = NOW + days(90)))
        }
    }

    @Test
    fun `um relogio atrasado nao antecipa o convite`() {
        assertFalse(canInviteToReview(declined(times = 1), now = NOW - days(365)))
    }

    @Test
    fun `aceitar guarda a resposta antes de abrir a loja`() = runBlocking {
        val steps = mutableListOf<String>()
        val repository = FakeReviewInviteRepository(onChange = { steps += "aceite guardado" })
        val store = object : StoreReview {
            override suspend fun open() {
                steps += "loja aberta"
            }
        }

        AcceptReviewInviteUseCase(repository, store)()

        assertEquals(listOf("aceite guardado", "loja aberta"), steps)
        assertFalse(ShouldInviteToReviewUseCase(repository) { NOW + days(3650) }())
    }

    @Test
    fun `recusar conta as recusas e guarda a hora da ultima`() = runBlocking {
        val repository = FakeReviewInviteRepository()
        var now = NOW
        val decline = DeclineReviewInviteUseCase(repository) { now }
        val shouldInvite = ShouldInviteToReviewUseCase(repository) { now }

        decline()

        assertEquals(ReviewInviteHistory(declineCount = 1, lastDeclinedAt = NOW), repository.history())
        assertFalse(shouldInvite())

        now += days(7)
        assertTrue(shouldInvite())

        decline()

        assertEquals(ReviewInviteHistory(declineCount = 2, lastDeclinedAt = now), repository.history())
        assertFalse(shouldInvite())
    }

    private fun declined(times: Int) =
        ReviewInviteHistory(declineCount = times, lastDeclinedAt = NOW)

    private fun days(count: Long) = TimeUnit.DAYS.toMillis(count)

    private companion object {
        const val NOW = 1_763_000_000_000L
    }
}

/** Respostas ao convite guardadas em memória; [onChange] avisa a cada gravação. */
private class FakeReviewInviteRepository(
    private val onChange: () -> Unit = {}
) : ReviewInviteRepository {

    private var current = ReviewInviteHistory()

    override suspend fun history() = current

    override suspend fun markAccepted() {
        current = current.copy(accepted = true)
        onChange()
    }

    override suspend fun markDeclined(atMillis: Long) {
        current = current.copy(declineCount = current.declineCount + 1, lastDeclinedAt = atMillis)
        onChange()
    }
}
