package com.jammes.calctmb.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import com.jammes.calctmb.R
import com.jammes.calctmb.ui.theme.AppTheme

/**
 * Usado tanto ao salvar um cálculo quanto ao renomear um já salvo. O nome é
 * sempre opcional, então o botão de confirmar nunca fica bloqueado.
 */
@Composable
fun NameInputDialog(
    title: String,
    initialName: String?,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable(initialName) { mutableStateOf(initialName.orEmpty()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null) },
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = name,
                    onValueChange = { if (it.length <= MAX_NAME_LENGTH) name = it },
                    label = { Text(stringResource(R.string.nome_opcional)) },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.nome_opcional_ajuda),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancelar)) }
        }
    )
}

private const val MAX_NAME_LENGTH = 40

@Preview
@Composable
private fun NameInputDialogPreview() {
    AppTheme {
        NameInputDialog(
            title = "Salvar resultado",
            initialName = null,
            confirmLabel = "Salvar",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
