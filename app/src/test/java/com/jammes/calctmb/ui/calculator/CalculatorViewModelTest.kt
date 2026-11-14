package com.jammes.calctmb.ui.calculator

import com.jammes.calctmb.domain.analytics.AnalyticsEvent
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.model.ReviewInviteHistory
import com.jammes.calctmb.domain.repository.IntroRepository
import com.jammes.calctmb.domain.repository.MeasurementRepository
import com.jammes.calctmb.domain.repository.ReviewInviteRepository
import com.jammes.calctmb.domain.repository.StoreReview
import com.jammes.calctmb.domain.usecase.AcceptReviewInviteUseCase
import com.jammes.calctmb.domain.usecase.CalculateBmrUseCase
import com.jammes.calctmb.domain.usecase.DeclineReviewInviteUseCase
import com.jammes.calctmb.domain.usecase.DeleteMeasurementUseCase
import com.jammes.calctmb.domain.usecase.MarkIntroAsShownUseCase
import com.jammes.calctmb.domain.usecase.ObserveMeasurementsUseCase
import com.jammes.calctmb.domain.usecase.RenameMeasurementUseCase
import com.jammes.calctmb.domain.usecase.SaveMeasurementUseCase
import com.jammes.calctmb.domain.usecase.ShouldInviteToReviewUseCase
import com.jammes.calctmb.domain.usecase.ShouldShowIntroUseCase
import com.jammes.calctmb.domain.usecase.ValidateBodyProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val repository = FakeMeasurementRepository()
    private val reviewInvites = FakeReviewInviteRepository()
    private val store = FakeStoreReview()
    private val tracked = mutableListOf<AnalyticsEvent>()
    private val reported = mutableListOf<Throwable>()
    private var now = 0L
    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val intro = FakeIntroRepository()
        viewModel = CalculatorViewModel(
            validateBodyProfile = ValidateBodyProfileUseCase(),
            calculateBmr = CalculateBmrUseCase(),
            saveMeasurement = SaveMeasurementUseCase(repository) { 0L },
            observeMeasurements = ObserveMeasurementsUseCase(repository),
            deleteMeasurement = DeleteMeasurementUseCase(repository),
            renameMeasurement = RenameMeasurementUseCase(repository),
            shouldShowIntro = ShouldShowIntroUseCase(intro),
            markIntroAsShown = MarkIntroAsShownUseCase(intro),
            shouldInviteToReview = ShouldInviteToReviewUseCase(reviewInvites) { now },
            acceptReviewInvite = AcceptReviewInviteUseCase(reviewInvites, store),
            declineReviewInvite = DeclineReviewInviteUseCase(reviewInvites) { now },
            analytics = { tracked += it },
            crashReporter = { reported += it }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calculo valido registra bmr_calculated`() {
        fillValidForm()

        viewModel.onAction(CalculatorAction.Calculate)

        assertEquals(listOf(AnalyticsEvent.BmrCalculated), tracked)
    }

    @Test
    fun `formulario invalido registra so quais campos falharam`() {
        viewModel.onAction(CalculatorAction.AgeChanged("30"))

        viewModel.onAction(CalculatorAction.Calculate)

        assertEquals(
            listOf(AnalyticsEvent.InvalidForm(setOf(BodyProfileField.WEIGHT, BodyProfileField.HEIGHT))),
            tracked
        )
    }

    @Test
    fun `limpar e abrir o sobre registram eventos`() {
        viewModel.onAction(CalculatorAction.ClearForm)
        viewModel.onAction(CalculatorAction.AboutClicked)

        assertEquals(listOf(AnalyticsEvent.FormCleared, AnalyticsEvent.AboutOpened), tracked)
    }

    @Test
    fun `cada acao no historico registra um evento`() {
        saveCalculation()
        val id = viewModel.uiState.value.history.single().id

        viewModel.onAction(CalculatorAction.RenameClicked(id))
        viewModel.onAction(CalculatorAction.ConfirmRename("Depois da dieta"))
        viewModel.onAction(CalculatorAction.ReuseMeasurement(id))
        viewModel.onAction(CalculatorAction.DeleteClicked(id))
        viewModel.onAction(CalculatorAction.ConfirmDelete)

        assertEquals(
            listOf(
                AnalyticsEvent.BmrCalculated,
                AnalyticsEvent.MeasurementSaved,
                AnalyticsEvent.MeasurementRenamed,
                AnalyticsEvent.MeasurementReused,
                AnalyticsEvent.MeasurementDeleted
            ),
            tracked
        )
        assertTrue(reported.isEmpty())
    }

    @Test
    fun `falha ao salvar vai para o Crashlytics e a tela avisa`() = runTest {
        repository.failure = IllegalStateException("disco cheio")

        saveCalculation()

        assertEquals(listOf("disco cheio"), reported.map { it.message })
        assertEquals(CalculatorEvent.Failed, viewModel.events.first())
        // O diálogo continua aberto para o usuário tentar de novo.
        assertEquals(CalculatorDialog.Save, viewModel.uiState.value.dialog)
        assertEquals(listOf(AnalyticsEvent.BmrCalculated), tracked)
    }

    @Test
    fun `salvar um calculo convida a avaliar o app depois da confirmacao`() = runTest {
        saveCalculation()

        // Primeiro só a confirmação do salvamento; o convite entra um instante depois.
        assertNull(viewModel.uiState.value.dialog)

        advanceUntilIdle()

        assertEquals(CalculatorDialog.ReviewInvite, viewModel.uiState.value.dialog)
        assertEquals(AnalyticsEvent.ReviewInviteShown, tracked.last())
    }

    @Test
    fun `avaliar fecha o convite e abre a loja e o convite nao volta`() = runTest {
        saveCalculation()
        advanceUntilIdle()

        viewModel.onAction(CalculatorAction.RateAppClicked)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.dialog)
        assertEquals(1, store.opened)
        assertTrue(reviewInvites.history().accepted)

        now += TimeUnit.DAYS.toMillis(365)
        saveCalculation()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.dialog)
        assertEquals(
            listOf(AnalyticsEvent.ReviewInviteShown, AnalyticsEvent.ReviewInviteAccepted),
            tracked.filter { it.name.startsWith("review_") }
        )
    }

    @Test
    fun `agora nao fecha o convite e ele so volta depois da espera`() = runTest {
        saveCalculation()
        advanceUntilIdle()

        viewModel.onAction(CalculatorAction.DismissDialog)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.dialog)
        assertEquals(0, store.opened)
        assertEquals(1, reviewInvites.history().declineCount)

        now += TimeUnit.DAYS.toMillis(6)
        saveCalculation()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.dialog)

        now += TimeUnit.DAYS.toMillis(1)
        saveCalculation()
        advanceUntilIdle()

        assertEquals(CalculatorDialog.ReviewInvite, viewModel.uiState.value.dialog)
        assertEquals(
            listOf(
                AnalyticsEvent.ReviewInviteShown,
                AnalyticsEvent.ReviewInviteDeclined,
                AnalyticsEvent.ReviewInviteShown
            ),
            tracked.filter { it.name.startsWith("review_") }
        )
    }

    @Test
    fun `o convite nao entra por cima de outro dialogo`() = runTest {
        saveCalculation()
        viewModel.onAction(CalculatorAction.AboutClicked)

        advanceUntilIdle()

        assertEquals(CalculatorDialog.About(firstLaunch = false), viewModel.uiState.value.dialog)
        assertFalse(AnalyticsEvent.ReviewInviteShown in tracked)

        // Fechar o "Sobre" não conta como recusa do convite.
        viewModel.onAction(CalculatorAction.DismissDialog)
        advanceUntilIdle()

        assertEquals(ReviewInviteHistory(), reviewInvites.history())
    }

    @Test
    fun `falha no convite vai para o Crashlytics sem avisar a tela`() = runTest {
        val received = mutableListOf<CalculatorEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.toList(received)
        }
        reviewInvites.failure = IllegalStateException("preferências ilegíveis")

        saveCalculation()
        advanceUntilIdle()

        // O salvamento deu certo, então a tela só confirma, sem "algo deu errado".
        assertEquals(listOf(CalculatorEvent.Saved), received)
        assertEquals(listOf("preferências ilegíveis"), reported.map { it.message })
        assertNull(viewModel.uiState.value.dialog)
    }

    private fun fillValidForm() {
        viewModel.onAction(CalculatorAction.AgeChanged("30"))
        viewModel.onAction(CalculatorAction.WeightChanged("80"))
        viewModel.onAction(CalculatorAction.HeightChanged("180"))
    }

    private fun saveCalculation() {
        fillValidForm()
        viewModel.onAction(CalculatorAction.Calculate)
        viewModel.onAction(CalculatorAction.SaveClicked)
        viewModel.onAction(CalculatorAction.ConfirmSave("Antes da dieta"))
    }
}

private class FakeMeasurementRepository : MeasurementRepository {

    private val measurements = MutableStateFlow<List<Measurement>>(emptyList())
    private var nextId = 1L

    /** Quando preenchida, toda gravação falha com ela. */
    var failure: Throwable? = null

    override fun observeAll(): Flow<List<Measurement>> = measurements

    override suspend fun save(measurement: Measurement): Long {
        failure?.let { throw it }
        val id = nextId++
        measurements.update { listOf(measurement.copy(id = id)) + it }
        return id
    }

    override suspend fun delete(id: Long) {
        failure?.let { throw it }
        measurements.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun updateName(id: Long, name: String?) {
        failure?.let { throw it }
        measurements.update { list -> list.map { if (it.id == id) it.copy(name = name) else it } }
    }
}

/** O diálogo inicial já foi visto, para não interferir nos testes. */
private class FakeIntroRepository : IntroRepository {
    override suspend fun wasIntroShown() = true
    override suspend fun markIntroAsShown() = Unit
}

/** Respostas ao convite de avaliação em memória. */
private class FakeReviewInviteRepository : ReviewInviteRepository {

    private var history = ReviewInviteHistory()

    /** Quando preenchida, toda leitura e gravação falha com ela. */
    var failure: Throwable? = null

    override suspend fun history(): ReviewInviteHistory {
        failure?.let { throw it }
        return history
    }

    override suspend fun markAccepted() {
        failure?.let { throw it }
        history = history.copy(accepted = true)
    }

    override suspend fun markDeclined(atMillis: Long) {
        failure?.let { throw it }
        history = history.copy(declineCount = history.declineCount + 1, lastDeclinedAt = atMillis)
    }
}

/** Conta quantas vezes a loja foi aberta. */
private class FakeStoreReview : StoreReview {

    var opened = 0
        private set

    override suspend fun open() {
        opened++
    }
}
