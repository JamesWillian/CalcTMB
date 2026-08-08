package com.jammes.calctmb.ui.calculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.ui.calculator.MeasurementItem
import com.jammes.calctmb.ui.theme.BrandSurface
import com.jammes.calctmb.ui.util.formatDateTime
import com.jammes.calctmb.ui.util.formatHeight
import com.jammes.calctmb.ui.util.formatKcal
import com.jammes.calctmb.ui.util.formatWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementDetailsSheet(
    item: MeasurementItem,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onReuse: () -> Unit,
    onDelete: () -> Unit
) {
    val measurement = item.measurement
    val profile = measurement.profile

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SexAvatar(profile.sex, size = 48)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (measurement.name != null) {
                        Text(
                            text = measurement.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.sem_nome),
                            style = MaterialTheme.typography.titleLarge,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = formatDateTime(measurement.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mesmo cartão navy da tela principal, para o valor ser reconhecido
            // como "o resultado" em qualquer lugar do app.
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = BrandSurface.Navy,
                    contentColor = BrandSurface.OnNavy
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(R.string.tmb).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandSurface.OnNavyMuted
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formatKcal(item.result.bmr),
                                style = MaterialTheme.typography.headlineMedium,
                                color = BrandSurface.Accent
                            )
                            Text(
                                text = stringResource(R.string.kcal_dia),
                                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BrandSurface.OnNavyMuted
                            )
                        }
                    }

                    HorizontalDivider(color = BrandSurface.OnNavy.copy(alpha = 0.15f))

                    CalorieGoals(
                        result = item.result,
                        labelColor = BrandSurface.OnNavyMuted,
                        valueColor = BrandSurface.OnNavy
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel(stringResource(R.string.seus_dados))
                DetailRow(
                    label = stringResource(R.string.sexo),
                    value = stringResource(
                        if (profile.sex == Sex.MALE) R.string.masculino else R.string.feminino
                    )
                )
                DetailRow(
                    label = stringResource(R.string.idade),
                    value = stringResource(R.string.valor_anos, profile.age)
                )
                DetailRow(
                    label = stringResource(R.string.peso),
                    value = stringResource(R.string.valor_kg, formatWeight(profile.weightKg))
                )
                DetailRow(
                    label = stringResource(R.string.altura),
                    value = stringResource(R.string.valor_cm, formatHeight(profile.heightCm))
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onRename, shape = MaterialTheme.shapes.small) {
                    Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null)
                    Text(
                        text = stringResource(R.string.renomear),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                OutlinedButton(onClick = onReuse, shape = MaterialTheme.shapes.small) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null)
                    Text(
                        text = stringResource(R.string.usar_novamente),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = stringResource(R.string.excluir),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.titleSmall)
    }
}
