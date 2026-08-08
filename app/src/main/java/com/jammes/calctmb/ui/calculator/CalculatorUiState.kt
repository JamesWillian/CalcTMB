package com.jammes.calctmb.ui.calculator

import com.jammes.calctmb.domain.model.BmrResult
import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.BodyProfileInput
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.model.Sex

/**
 * Um cálculo salvo junto com o gasto calórico recalculado a partir dos dados
 * gravados — assim o histórico nunca fica fora de sincronia com a fórmula.
 */
data class MeasurementItem(
    val measurement: Measurement,
    val result: BmrResult
) {
    val id: Long get() = measurement.id
}

sealed interface CalculatorDialog {
    /** Explicação sobre a TMB. [firstLaunch] marca a exibição automática inicial. */
    data class About(val firstLaunch: Boolean) : CalculatorDialog
    data object Save : CalculatorDialog
    data class Rename(val id: Long, val currentName: String?) : CalculatorDialog
    data class ConfirmDelete(val id: Long) : CalculatorDialog
}

data class CalculatorUiState(
    val input: BodyProfileInput = BodyProfileInput(
        sex = Sex.MALE,
        age = "",
        weightKg = "",
        heightCm = ""
    ),
    val invalidFields: Set<BodyProfileField> = emptySet(),
    /** Perfil usado no último cálculo, mantido para poder salvá-lo. */
    val calculatedProfile: BodyProfile? = null,
    val result: BmrResult? = null,
    val isCurrentResultSaved: Boolean = false,
    val history: List<MeasurementItem> = emptyList(),
    val selectedId: Long? = null,
    val dialog: CalculatorDialog? = null
) {
    val selectedItem: MeasurementItem?
        get() = selectedId?.let { id -> history.firstOrNull { it.id == id } }
}

/** Avisos pontuais, consumidos uma única vez pela tela. */
sealed interface CalculatorEvent {
    data object Saved : CalculatorEvent
    data object Deleted : CalculatorEvent
    data object Renamed : CalculatorEvent
    data object InvalidForm : CalculatorEvent
}

sealed interface CalculatorAction {
    data class SexChanged(val sex: Sex) : CalculatorAction
    data class AgeChanged(val value: String) : CalculatorAction
    data class WeightChanged(val value: String) : CalculatorAction
    data class HeightChanged(val value: String) : CalculatorAction
    data object Calculate : CalculatorAction
    data object ClearForm : CalculatorAction

    data object SaveClicked : CalculatorAction
    data class ConfirmSave(val name: String) : CalculatorAction

    data object AboutClicked : CalculatorAction
    data object DismissDialog : CalculatorAction

    data class MeasurementClicked(val id: Long) : CalculatorAction
    data object DismissDetails : CalculatorAction
    data class ReuseMeasurement(val id: Long) : CalculatorAction

    data class RenameClicked(val id: Long) : CalculatorAction
    data class ConfirmRename(val name: String) : CalculatorAction
    data class DeleteClicked(val id: Long) : CalculatorAction
    data object ConfirmDelete : CalculatorAction
}
