package com.jammes.calctmb.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.domain.model.BmrResult
import com.jammes.calctmb.ui.theme.AppTheme
import com.jammes.calctmb.ui.theme.BrandSurface
import com.jammes.calctmb.ui.util.formatKcal

/**
 * O resultado é o momento alto da tela, então ganha a superfície de marca:
 * cartão navy, número em âmbar. Contraste igual nos dois temas.
 */
@Composable
fun ResultCard(
    result: BmrResult,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = BrandSurface.Navy,
            contentColor = BrandSurface.OnNavy
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.hint_resultado_tmb),
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandSurface.OnNavyMuted
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatKcal(result.bmr),
                        style = MaterialTheme.typography.displaySmall,
                        color = BrandSurface.Accent
                    )
                    Text(
                        text = stringResource(R.string.kcal_dia),
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = BrandSurface.OnNavyMuted
                    )
                }
            }

            HorizontalDivider(color = BrandSurface.OnNavy.copy(alpha = 0.15f))

            SectionLabelOnNavy(stringResource(R.string.gasto_calorico_total))

            CalorieGoals(
                result = result,
                labelColor = BrandSurface.OnNavyMuted,
                valueColor = BrandSurface.OnNavy
            )

            SaveButton(isSaved = isSaved, onClick = onSaveClick)
        }
    }
}

@Composable
private fun SaveButton(isSaved: Boolean, onClick: () -> Unit) {
    val label = stringResource(if (isSaved) R.string.salvo else R.string.salvar)
    val icon = if (isSaved) Icons.Outlined.CheckCircle else Icons.Outlined.BookmarkAdd

    if (isSaved) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            enabled = false,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, BrandSurface.OnNavy.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                disabledContentColor = BrandSurface.OnNavyMuted
            )
        ) {
            Icon(icon, contentDescription = null)
            Text(label, modifier = Modifier.padding(start = 8.dp))
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandSurface.OnNavy,
                contentColor = BrandSurface.Navy
            )
        ) {
            Icon(icon, contentDescription = null)
            Text(
                text = label,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun SectionLabelOnNavy(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = BrandSurface.OnNavyMuted
    )
}

/**
 * As três metas derivadas da TMB, usadas no resultado atual e nos detalhes de
 * um cálculo salvo — daí as cores serem parâmetro, já que as duas superfícies
 * são diferentes.
 */
@Composable
fun CalorieGoals(
    result: BmrResult,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    valueColor: Color = LocalContentColor.current
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GoalRow(stringResource(R.string.ganho_peso), result.weightGain, labelColor, valueColor)
        GoalRow(stringResource(R.string.hipertrofia), result.hypertrophy, labelColor, valueColor)
        GoalRow(stringResource(R.string.emagrecimento), result.weightLoss, labelColor, valueColor)
    }
}

@Composable
private fun GoalRow(
    label: String,
    value: Double,
    labelColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = labelColor)
        Text(
            text = stringResource(R.string.resultado_kcal, formatKcal(value)),
            style = MaterialTheme.typography.titleSmall,
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultCardPreview() {
    AppTheme {
        ResultCard(
            result = BmrResult(
                bmr = 1798.0,
                weightGain = 2517.2,
                hypertrophy = 2157.6,
                weightLoss = 1438.4
            ),
            isSaved = false,
            onSaveClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
