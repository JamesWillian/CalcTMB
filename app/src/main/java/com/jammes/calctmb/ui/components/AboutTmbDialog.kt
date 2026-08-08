package com.jammes.calctmb.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.ui.theme.AppTheme

@Composable
fun AboutTmbDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.entendi))
            }
        },
        title = { Text(stringResource(R.string.o_que_eh_tmb)) },
        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.importante),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.importante_sobre))

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.tmb),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.sobre_tmb))

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.gasto_calorico_total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.sobre_gasto_calorico))

                    Spacer(Modifier.height(12.dp))

                    BulletPoint(stringResource(R.string.ganhar_peso_sobre))
                    BulletPoint(stringResource(R.string.hipertrofia_sobre))
                    BulletPoint(stringResource(R.string.perder_peso_sobre))
                }
            }
        }
    )
}

@Composable
private fun BulletPoint(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("• ", fontWeight = FontWeight.Bold)
        Text(text)
    }
}

@Preview
@Composable
private fun AboutTmbDialogPreview() {
    AppTheme {
        AboutTmbDialog(onDismiss = {})
    }
}
