package com.jammes.calctmb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel: ViewModel() {

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                tmb = "",
                ganhoPeso = "",
                hipertrofia = "",
                emagrecimento = ""
            )
        )
    }

    fun getUiState(): LiveData<UiState> {
        return uiState
    }

    fun onResume() {
        uiState.value = getUiState().value
    }

    fun calcularTMB(idade: Int, peso: Double, altura: Double, sexo: String) {
        val resultado = when (sexo) {
            "Masculino" -> 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * idade)
            "Feminino" -> 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * idade)
            else -> 0.0
        }

        val tmb = "%.2f".format(resultado)
        val ganho = "%.2f".format(resultado * 1.4)
        val hipertrofia = "%.2f".format(resultado * 1.2)
        val emagrecimento = "%.2f".format(resultado * 0.8)

        uiState.postValue(UiState(tmb, ganho, hipertrofia, emagrecimento, true))
    }

    data class UiState(
        val tmb: String,
        val ganhoPeso: String,
        val hipertrofia: String,
        val emagrecimento: String,
        val resultadoVisivel: Boolean = false
    )

    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }
}