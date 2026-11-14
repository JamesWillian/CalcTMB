package com.jammes.calctmb.ui.calculator

import com.jammes.calctmb.domain.analytics.AnalyticsEvent
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.repository.IntroRepository
import com.jammes.calctmb.domain.repository.MeasurementRepository
import com.jammes.calctmb.domain.usecase.CalculateBmrUseCase
import com.jammes.calctmb.domain.usecase.DeleteMeasurementUseCase
import com.jammes.calctmb.domain.usecase.MarkIntroAsShownUseCase
import com.jammes.calctmb.domain.usecase.ObserveMeasurementsUseCase
import com.jammes.calctmb.domain.usecase.RenameMeasurementUseCase
import com.jammes.calctmb.domain.usecase.SaveMeasurementUseCase
import com.jammes.calctmb.domain.usecase.ShouldShowIntroUseCase
import com.jammes.calctmb.domain.usecase.ValidateBodyProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val repository = FakeMeasurementRepository()
    private val tracked = mutableListOf<AnalyticsEvent>()
    private val reported = mutableListOf<Throwable>()
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
