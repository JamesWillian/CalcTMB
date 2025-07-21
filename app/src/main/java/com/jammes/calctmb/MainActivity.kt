package com.jammes.calctmb

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jammes.calctmb.ui.theme.AppTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
            ShowAboutDialogOnce()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    @Composable
    fun ShowAboutDialogOnce() {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val wasShown = prefs.getBoolean("showAbout", false)

            if (!wasShown) {
                showDialog = true
                prefs.edit { putBoolean("showAbout", true) }
            }
        }

        SobreTMBDialog(showDialog, onDismiss = { showDialog = false })

    }

}

@Composable
fun SobreTMBDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AppTheme {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.entendi))
                    }
                },
                title = { Text(stringResource(R.string.o_que_eh_tmb)) },
                icon = {
                    Icon(Icons.Default.Info, contentDescription = null)
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                        ) {

                            Text( // Aviso importante
                                text = stringResource(R.string.importante),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.importante_sobre)
                            )

                            Spacer(Modifier.height(16.dp))

                            Text( // Título TMB
                                text = stringResource(R.string.tmb),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text( // Sobre o TMB
                                text = stringResource(R.string.sobre_tmb)
                            )

                            Spacer(Modifier.height(16.dp))

                            Text( // Título Gasto Calórico
                                text = stringResource(R.string.gasto_calorico_total),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text( // Sobre o Gasto Calórico
                                text = stringResource(R.string.sobre_gasto_calorico)
                            )

                            Spacer(Modifier.height(12.dp))

                            // Lista de Bullet Points (Ganho, Hipertrofia, Perda peso)
                            BulletPoint(text = stringResource(R.string.ganhar_peso_sobre))
                            BulletPoint(text = stringResource(R.string.hipertrofia_sobre))
                            BulletPoint(text = stringResource(R.string.perder_peso_sobre))

                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("• ", fontWeight = FontWeight.Bold)
        Text(text)
    }
}


@Preview
@Composable
private fun DialogPreview() {
    SobreTMBDialog(true, onDismiss = { false })
}

@Composable
fun App(modifier: Modifier = Modifier) {
    AppTheme {
        Scaffold(modifier.fillMaxSize()) { innerPadding ->
            MainScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.getUiState().observeAsState()

    var idade by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("M") } //Definição do sexo padrão no chip

    var erroIdade by remember { mutableStateOf(false) }
    var erroPeso by remember { mutableStateOf(false) }
    var erroAltura by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Imagem de fundo
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Logo e título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_tmb),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.calculadora_tmb),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Spacer(Modifier.height(24.dp))

                // Chip sexo
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    FilterChip(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .heightIn(min = 40.dp),
                        selected = sexo == "M",
                        onClick = { sexo = "M" },
                        label = { Text(stringResource(R.string.masculino)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Male, contentDescription = null)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .heightIn(min = 40.dp),
                        selected = sexo == "F",
                        onClick = { sexo = "F" },
                        label = { Text(stringResource(R.string.feminino)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Female, contentDescription = null)
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Idade
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = idade,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) { // Aceitar somente dígitos
                            val valor = input.toIntOrNull()
                            if (valor == null || valor in 1..120) { // Limita a idade máxima a 120 anos
                                idade = input
                                erroIdade = false
                            }
                        }
                    },
                    isError = erroIdade,
                    label = { Text(stringResource(R.string.idade)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                if (erroIdade) {
                    Text(
                        stringResource(R.string.idade_invalida),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Peso
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = peso,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) { // Aceitar somente dígitos
                            val valor = input.toIntOrNull()
                            if (valor == null || valor in 1..500) { // Limita o peso máximo a 500 kg
                                peso = input
                                erroPeso = false
                            }
                        }
                    },
                    isError = erroPeso,
                    label = { Text(stringResource(R.string.peso_kg)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                if (erroPeso) {
                    Text(
                        stringResource(R.string.peso_invalido),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Altura
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = altura,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) { // Aceitar somente dígitos
                            val valor = input.toIntOrNull()
                            if (valor == null || valor in 1..300) { // Limita a altura máxima a 300 cm
                                altura = input
                                erroAltura = false
                            }
                        }
                    },
                    isError = erroAltura,
                    label = { Text(stringResource(R.string.altura_cm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                if (erroAltura) {
                    Text(
                        stringResource(R.string.altura_invalida),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Botão calcular
                Button(
                    onClick = {
                        var invalido = false

                        if (idade.isBlank() || idade.toInt() > 120 || idade.toInt() < 1) {
                            invalido = true
                            erroIdade = true
                        }

                        if (peso.isBlank() || peso.toInt() > 500 || peso.toInt() < 1) {
                            invalido = true
                            erroPeso = true
                        }

                        if (altura.isBlank() || altura.toInt() > 300 || altura.toInt() < 1) {
                            invalido = true
                            erroAltura = true
                        }

                        if (invalido) return@Button

                        keyboardController?.hide()
                        viewModel.calcularTMB(
                            idade.toInt(),
                            peso.toDouble(),
                            altura.toDouble(), sexo
                        )

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.calcular))
                }

                Spacer(Modifier.height(24.dp))

            }

            // Resultados
            if (uiState?.resultadoVisivel == true) {
                Resultados(uiState!!)
            }
        }
    }
}

@Composable
fun Resultados(uiState: MainViewModel.UiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.hint_resultado_tmb), color = Color.White)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var showSobreDialog by remember { mutableStateOf(false) }

            Text(
                text = stringResource(R.string.resultado_kcal, uiState.tmb),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Yellow
            )
            IconButton(onClick = { showSobreDialog = true }) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
            }

            SobreTMBDialog(showSobreDialog, onDismiss = { showSobreDialog = false })

        }
        Spacer(Modifier.height(8.dp))
        Text(
            "${stringResource(R.string.ganho_peso)} ${
                stringResource(
                    R.string.resultado_kcal,
                    uiState.ganhoPeso
                )
            }", color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${stringResource(R.string.hipertrofia)} ${
                stringResource(
                    R.string.resultado_kcal,
                    uiState.hipertrofia
                )
            }", color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${stringResource(R.string.emagrecimento)} ${
                stringResource(
                    R.string.resultado_kcal,
                    uiState.emagrecimento
                )
            }", color = Color.White
        )
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}

@Preview
@Composable
private fun ResultadosPreview(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.getUiState().observeAsState()
    Resultados(uiState!!)
}
