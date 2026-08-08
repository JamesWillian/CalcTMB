package com.jammes.calctmb.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.domain.model.BmrResult
import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.ui.calculator.MeasurementItem
import com.jammes.calctmb.ui.theme.AppTheme
import com.jammes.calctmb.ui.util.formatDate
import com.jammes.calctmb.ui.util.formatHeight
import com.jammes.calctmb.ui.util.formatKcal
import com.jammes.calctmb.ui.util.formatWeight

@Composable
fun MeasurementCard(
    item: MeasurementItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = item.measurement.profile

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SexAvatar(profile.sex)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.measurement.name ?: formatDate(item.measurement.createdAt),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(
                        stringResource(R.string.valor_anos, profile.age),
                        stringResource(R.string.valor_kg, formatWeight(profile.weightKg)),
                        stringResource(R.string.valor_cm, formatHeight(profile.heightCm))
                    ).joinToString("  ·  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = formatKcal(item.result.bmr),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.kcal_dia),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SexAvatar(
    sex: Sex,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    Surface(
        modifier = modifier.size(size.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (sex == Sex.MALE) Icons.Filled.Male else Icons.Filled.Female,
                contentDescription = stringResource(
                    if (sex == Sex.MALE) R.string.masculino else R.string.feminino
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MeasurementCardPreview() {
    AppTheme {
        MeasurementCard(
            item = MeasurementItem(
                measurement = Measurement(
                    id = 1,
                    name = "Antes da dieta",
                    createdAt = 1_754_500_000_000,
                    profile = BodyProfile(Sex.MALE, 32, 78.5, 180.0)
                ),
                result = BmrResult(1798.0, 2517.2, 2157.6, 1438.4)
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
