package com.jammes.calctmb.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.domain.model.ValidationResult
import com.jammes.calctmb.domain.usecase.CalculateBmrUseCase
import com.jammes.calctmb.domain.usecase.DeleteMeasurementUseCase
import com.jammes.calctmb.domain.usecase.MarkIntroAsShownUseCase
import com.jammes.calctmb.domain.usecase.ObserveMeasurementsUseCase
import com.jammes.calctmb.domain.usecase.RenameMeasurementUseCase
import com.jammes.calctmb.domain.usecase.SaveMeasurementUseCase
import com.jammes.calctmb.domain.usecase.ShouldShowIntroUseCase
import com.jammes.calctmb.domain.usecase.ValidateBodyProfileUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val validateBodyProfile: ValidateBodyProfileUseCase,
    private val calculateBmr: CalculateBmrUseCase,
    private val saveMeasurement: SaveMeasurementUseCase,
    private val observeMeasurements: ObserveMeasurementsUseCase,
    private val deleteMeasurement: DeleteMeasurementUseCase,
    private val renameMeasurement: RenameMeasurementUseCase,
    private val shouldShowIntro: ShouldShowIntroUseCase,
    private val markIntroAsShown: MarkIntroAsShownUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _events = Channel<CalculatorEvent>(Channel.BUFFERED)
    val events: Flow<CalculatorEvent> = _events.receiveAsFlow()

    init {
        observeHistory()
        showIntroIfNeeded()
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.SexChanged -> onSexChanged(action.sex)
            is CalculatorAction.AgeChanged -> onAgeChanged(action.value)
            is CalculatorAction.WeightChanged -> onWeightChanged(action.value)
            is CalculatorAction.HeightChanged -> onHeightChanged(action.value)
            CalculatorAction.Calculate -> calculate()
            CalculatorAction.ClearForm -> clearForm()

            CalculatorAction.SaveClicked -> openSaveDialog()
            is CalculatorAction.ConfirmSave -> save(action.name)

            CalculatorAction.AboutClicked -> _uiState.update {
                it.copy(dialog = CalculatorDialog.About(firstLaunch = false))
            }

            CalculatorAction.DismissDialog -> dismissDialog()

            is CalculatorAction.MeasurementClicked -> _uiState.update {
                it.copy(selectedId = action.id)
            }

            CalculatorAction.DismissDetails -> _uiState.update { it.copy(selectedId = null) }
            is CalculatorAction.ReuseMeasurement -> reuse(action.id)

            is CalculatorAction.RenameClicked -> openRenameDialog(action.id)
            is CalculatorAction.ConfirmRename -> rename(action.name)
            is CalculatorAction.DeleteClicked -> _uiState.update {
                it.copy(dialog = CalculatorDialog.ConfirmDelete(action.id))
            }

            CalculatorAction.ConfirmDelete -> confirmDelete()
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            observeMeasurements().collect { measurements ->
                val items = measurements.map { measurement ->
                    MeasurementItem(measurement, calculateBmr(measurement.profile))
                }
                _uiState.update { state ->
                    state.copy(
                        history = items,
                        // O item aberto pode ter sido excluído em outro fluxo.
                        selectedId = state.selectedId?.takeIf { id -> items.any { it.id == id } }
                    )
                }
            }
        }
    }

    private fun showIntroIfNeeded() {
        viewModelScope.launch {
            if (shouldShowIntro()) {
                _uiState.update { it.copy(dialog = CalculatorDialog.About(firstLaunch = true)) }
            }
        }
    }

    private fun onSexChanged(sex: Sex) = _uiState.update {
        it.copy(input = it.input.copy(sex = sex)).withResultCleared()
    }

    private fun onAgeChanged(value: String) {
        val digits = value.filter { it.isDigit() }.take(MAX_AGE_LENGTH)
        _uiState.update {
            it.copy(
                input = it.input.copy(age = digits),
                invalidFields = it.invalidFields - BodyProfileField.AGE
            ).withResultCleared()
        }
    }

    private fun onWeightChanged(value: String) {
        _uiState.update {
            it.copy(
                input = it.input.copy(weightKg = value.sanitizeDecimal(MAX_WEIGHT_LENGTH)),
                invalidFields = it.invalidFields - BodyProfileField.WEIGHT
            ).withResultCleared()
        }
    }

    private fun onHeightChanged(value: String) {
        val digits = value.filter { it.isDigit() }.take(MAX_HEIGHT_LENGTH)
        _uiState.update {
            it.copy(
                input = it.input.copy(heightCm = digits),
                invalidFields = it.invalidFields - BodyProfileField.HEIGHT
            ).withResultCleared()
        }
    }

    private fun calculate() {
        when (val validation = validateBodyProfile(_uiState.value.input)) {
            is ValidationResult.Invalid -> {
                _uiState.update {
                    it.copy(invalidFields = validation.invalidFields).withResultCleared()
                }
                _events.trySend(CalculatorEvent.InvalidForm)
            }

            is ValidationResult.Valid -> _uiState.update {
                it.copy(
                    invalidFields = emptySet(),
                    calculatedProfile = validation.profile,
                    result = calculateBmr(validation.profile),
                    isCurrentResultSaved = false
                )
            }
        }
    }

    private fun clearForm() = _uiState.update {
        it.copy(
            input = CalculatorUiState().input,
            invalidFields = emptySet()
        ).withResultCleared()
    }

    private fun openSaveDialog() {
        if (_uiState.value.calculatedProfile == null) return
        _uiState.update { it.copy(dialog = CalculatorDialog.Save) }
    }

    private fun save(name: String) {
        val profile = _uiState.value.calculatedProfile ?: return
        viewModelScope.launch {
            saveMeasurement(profile, name)
            _uiState.update { it.copy(dialog = null, isCurrentResultSaved = true) }
            _events.send(CalculatorEvent.Saved)
        }
    }

    private fun openRenameDialog(id: Long) {
        val item = _uiState.value.history.firstOrNull { it.id == id } ?: return
        _uiState.update {
            it.copy(dialog = CalculatorDialog.Rename(id, item.measurement.name))
        }
    }

    private fun rename(name: String) {
        val dialog = _uiState.value.dialog as? CalculatorDialog.Rename ?: return
        viewModelScope.launch {
            renameMeasurement(dialog.id, name)
            _uiState.update { it.copy(dialog = null) }
            _events.send(CalculatorEvent.Renamed)
        }
    }

    private fun confirmDelete() {
        val dialog = _uiState.value.dialog as? CalculatorDialog.ConfirmDelete ?: return
        viewModelScope.launch {
            deleteMeasurement(dialog.id)
            _uiState.update { it.copy(dialog = null, selectedId = null) }
            _events.send(CalculatorEvent.Deleted)
        }
    }

    private fun reuse(id: Long) {
        val item = _uiState.value.history.firstOrNull { it.id == id } ?: return
        val profile = item.measurement.profile
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    sex = profile.sex,
                    age = profile.age.toString(),
                    weightKg = profile.weightKg.trimTrailingZero(),
                    heightCm = profile.heightCm.toInt().toString()
                ),
                invalidFields = emptySet(),
                calculatedProfile = profile,
                result = item.result,
                isCurrentResultSaved = true,
                selectedId = null
            )
        }
    }

    private fun dismissDialog() {
        val current = _uiState.value.dialog
        if (current is CalculatorDialog.About && current.firstLaunch) {
            viewModelScope.launch { markIntroAsShown() }
        }
        _uiState.update { it.copy(dialog = null) }
    }

    /** Um resultado só vale para os dados que o geraram. */
    private fun CalculatorUiState.withResultCleared(): CalculatorUiState =
        if (result == null) this
        else copy(result = null, calculatedProfile = null, isCurrentResultSaved = false)

    private fun String.sanitizeDecimal(maxLength: Int): String {
        var separatorUsed = false
        return buildString {
            for (char in this@sanitizeDecimal) {
                when {
                    char.isDigit() -> append(char)
                    (char == '.' || char == ',') && !separatorUsed && isNotEmpty() -> {
                        separatorUsed = true
                        append(char)
                    }
                }
                if (length == maxLength) break
            }
        }
    }

    private fun Double.trimTrailingZero(): String =
        if (this % 1.0 == 0.0) toInt().toString() else toString()

    private companion object {
        const val MAX_AGE_LENGTH = 3
        const val MAX_HEIGHT_LENGTH = 3
        const val MAX_WEIGHT_LENGTH = 6
    }
}
