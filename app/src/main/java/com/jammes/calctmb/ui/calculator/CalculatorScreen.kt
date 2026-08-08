package com.jammes.calctmb.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jammes.calctmb.R
import com.jammes.calctmb.domain.model.BmrResult
import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.BodyProfileInput
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.ui.calculator.components.AppHeader
import com.jammes.calctmb.ui.calculator.components.CalculatorForm
import com.jammes.calctmb.ui.calculator.components.EmptyHistory
import com.jammes.calctmb.ui.calculator.components.MeasurementCard
import com.jammes.calctmb.ui.calculator.components.MeasurementDetailsSheet
import com.jammes.calctmb.ui.calculator.components.ResultCard
import com.jammes.calctmb.ui.calculator.components.SectionLabel
import com.jammes.calctmb.ui.components.AboutTmbDialog
import com.jammes.calctmb.ui.components.NameInputDialog
import com.jammes.calctmb.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculatorRoute(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val message = context.getString(
                when (event) {
                    CalculatorEvent.Saved -> R.string.resultado_salvo
                    CalculatorEvent.Deleted -> R.string.resultado_excluido
                    CalculatorEvent.Renamed -> R.string.nome_atualizado
                    CalculatorEvent.InvalidForm -> R.string.corrija_campos
                }
            )
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    CalculatorScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        // A barra superior cuida do próprio inset de status bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AppHeader(onAboutClick = { onAction(CalculatorAction.AboutClicked) })
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp + bottomInset),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "form") {
                CalculatorForm(
                    input = uiState.input,
                    invalidFields = uiState.invalidFields,
                    onSexChange = { onAction(CalculatorAction.SexChanged(it)) },
                    onAgeChange = { onAction(CalculatorAction.AgeChanged(it)) },
                    onWeightChange = { onAction(CalculatorAction.WeightChanged(it)) },
                    onHeightChange = { onAction(CalculatorAction.HeightChanged(it)) },
                    onCalculate = {
                        keyboardController?.hide()
                        onAction(CalculatorAction.Calculate)
                    },
                    onClear = { onAction(CalculatorAction.ClearForm) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item(key = "result") {
                AnimatedVisibility(
                    visible = uiState.result != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    uiState.result?.let { result ->
                        ResultCard(
                            result = result,
                            isSaved = uiState.isCurrentResultSaved,
                            onSaveClick = { onAction(CalculatorAction.SaveClicked) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            item(key = "history_title") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(stringResource(R.string.historico))
                    if (uiState.history.isNotEmpty()) {
                        Text(
                            text = uiState.history.size.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (uiState.history.isEmpty()) {
                item(key = "history_empty") {
                    EmptyHistory(modifier = Modifier.padding(horizontal = 16.dp))
                }
            } else {
                items(items = uiState.history, key = { it.id }) { item ->
                    MeasurementCard(
                        item = item,
                        onClick = { onAction(CalculatorAction.MeasurementClicked(item.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    uiState.selectedItem?.let { item ->
        MeasurementDetailsSheet(
            item = item,
            onDismiss = { onAction(CalculatorAction.DismissDetails) },
            onRename = { onAction(CalculatorAction.RenameClicked(item.id)) },
            onReuse = { onAction(CalculatorAction.ReuseMeasurement(item.id)) },
            onDelete = { onAction(CalculatorAction.DeleteClicked(item.id)) }
        )
    }

    CalculatorDialogs(dialog = uiState.dialog, onAction = onAction)
}

@Composable
private fun CalculatorDialogs(
    dialog: CalculatorDialog?,
    onAction: (CalculatorAction) -> Unit
) {
    when (dialog) {
        null -> Unit

        is CalculatorDialog.About -> AboutTmbDialog(
            onDismiss = { onAction(CalculatorAction.DismissDialog) }
        )

        CalculatorDialog.Save -> NameInputDialog(
            title = stringResource(R.string.salvar_resultado),
            initialName = null,
            confirmLabel = stringResource(R.string.salvar),
            onConfirm = { onAction(CalculatorAction.ConfirmSave(it)) },
            onDismiss = { onAction(CalculatorAction.DismissDialog) }
        )

        is CalculatorDialog.Rename -> NameInputDialog(
            title = stringResource(R.string.renomear_calculo),
            initialName = dialog.currentName,
            confirmLabel = stringResource(R.string.confirmar),
            onConfirm = { onAction(CalculatorAction.ConfirmRename(it)) },
            onDismiss = { onAction(CalculatorAction.DismissDialog) }
        )

        is CalculatorDialog.ConfirmDelete -> AlertDialog(
            onDismissRequest = { onAction(CalculatorAction.DismissDialog) },
            title = { Text(stringResource(R.string.excluir_titulo)) },
            text = { Text(stringResource(R.string.excluir_mensagem)) },
            confirmButton = {
                TextButton(onClick = { onAction(CalculatorAction.ConfirmDelete) }) {
                    Text(
                        text = stringResource(R.string.excluir),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(CalculatorAction.DismissDialog) }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculatorScreenPreview() {
    AppTheme {
        CalculatorScreen(
            uiState = CalculatorUiState(
                input = BodyProfileInput(
                    sex = Sex.MALE,
                    age = "32",
                    weightKg = "78,5",
                    heightCm = "180"
                ),
                result = BmrResult(1798.0, 2517.2, 2157.6, 1438.4),
                history = listOf(
                    MeasurementItem(
                        measurement = Measurement(
                            id = 1,
                            name = "Antes da dieta",
                            createdAt = 1_754_500_000_000,
                            profile = BodyProfile(Sex.MALE, 32, 78.5, 180.0)
                        ),
                        result = BmrResult(1798.0, 2517.2, 2157.6, 1438.4)
                    )
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}
