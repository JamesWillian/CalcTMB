package com.jammes.calctmb.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.BodyProfileInput
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.ui.theme.AppTheme

@Composable
fun CalculatorForm(
    input: BodyProfileInput,
    invalidFields: Set<BodyProfileField>,
    onSexChange: (Sex) -> Unit,
    onAgeChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onCalculate: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        // Contorno em vez de sombra: superfícies planas e delimitadas envelhecem
        // melhor do que elevação pesada.
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel(stringResource(R.string.seus_dados))

            SexSelector(selected = input.sex, onSexChange = onSexChange)

            NumericField(
                value = input.age,
                onValueChange = onAgeChange,
                label = stringResource(R.string.idade),
                suffix = stringResource(R.string.unidade_anos),
                isError = BodyProfileField.AGE in invalidFields,
                errorMessage = stringResource(R.string.idade_invalida)
            )

            NumericField(
                value = input.weightKg,
                onValueChange = onWeightChange,
                label = stringResource(R.string.peso),
                suffix = stringResource(R.string.unidade_kg),
                isError = BodyProfileField.WEIGHT in invalidFields,
                errorMessage = stringResource(R.string.peso_invalido),
                keyboardType = KeyboardType.Decimal
            )

            NumericField(
                value = input.heightCm,
                onValueChange = onHeightChange,
                label = stringResource(R.string.altura),
                suffix = stringResource(R.string.unidade_cm),
                isError = BodyProfileField.HEIGHT in invalidFields,
                errorMessage = stringResource(R.string.altura_invalida),
                imeAction = ImeAction.Done,
                onImeAction = onCalculate
            )

            Button(
                onClick = onCalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.calcular),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            TextButton(
                onClick = onClear,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.limpar))
            }
        }
    }
}

/** Rótulo de seção: caixa alta e espaçada, para separar blocos sem pesar. */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SexSelector(
    selected: Sex,
    onSexChange: (Sex) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        Sex.MALE to stringResource(R.string.masculino),
        Sex.FEMALE to stringResource(R.string.feminino)
    )

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (sex, label) ->
            SegmentedButton(
                selected = sex == selected,
                onClick = { onSexChange(sex) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                icon = {
                    Icon(
                        imageVector = if (sex == Sex.MALE) Icons.Filled.Male else Icons.Filled.Female,
                        contentDescription = null
                    )
                }
            ) {
                Text(label)
            }
        }
    }
}

@Composable
private fun NumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = {
            Text(
                text = suffix,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        isError = isError,
        supportingText = if (isError) {
            { Text(errorMessage) }
        } else {
            null
        },
        textStyle = MaterialTheme.typography.titleMedium,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            cursorColor = MaterialTheme.colorScheme.secondary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
private fun CalculatorFormPreview() {
    AppTheme {
        CalculatorForm(
            input = BodyProfileInput(Sex.MALE, "32", "78,5", "180"),
            invalidFields = emptySet(),
            onSexChange = {},
            onAgeChange = {},
            onWeightChange = {},
            onHeightChange = {},
            onCalculate = {},
            onClear = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
