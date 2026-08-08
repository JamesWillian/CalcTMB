package com.jammes.calctmb.ui.calculator.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jammes.calctmb.R
import com.jammes.calctmb.ui.theme.AppTheme
import com.jammes.calctmb.ui.theme.BrandSurface

/**
 * Barra superior fixa: altura definida pelo conteúdo, o suficiente para as duas
 * linhas do título. Usa a superfície de marca (navy) nos dois temas — assim a
 * logo branca tem sempre o mesmo contraste e o topo do app não "pisca" de cor
 * quando o sistema alterna claro/escuro.
 */
@Composable
fun AppHeader(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BrandSurface.Navy,
        contentColor = BrandSurface.OnNavy
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_tmb),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.calculadora_tmb),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onAboutClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.o_que_eh_tmb)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppHeaderPreview() {
    AppTheme {
        AppHeader(onAboutClick = {})
    }
}
