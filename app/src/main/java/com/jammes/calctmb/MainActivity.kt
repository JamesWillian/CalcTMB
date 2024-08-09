package com.jammes.calctmb

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import com.jammes.calctmb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getUiState().observe(this) { uiState ->
            bindUiState(uiState)
        }

        binding.calcularButton.setOnClickListener {
            val idade = binding.idadeEditText.text.toString()
            val peso = binding.pesoEditText.text.toString()
            val altura = binding.alturaEditText.text.toString()
            val sexo = binding.chipsSexo.checkedChipId

            if (validarCampos(idade, peso, altura))
                return@setOnClickListener

            viewModel.calcularTMB(
                idade.toInt(),
                peso.toDouble(),
                altura.toDouble(),
                when (sexo) {
                    R.id.chipMasculino -> "Masculino"
                    R.id.chipFeminino -> "Feminino"
                    else -> ""
                }
            )

            hideKeyboard()

            binding.mainScroll.fullScroll(View.FOCUS_DOWN)
        }

        binding.sobreButton.setOnClickListener {
            showAboutDialog()
        }

        exibirMensagemInicial()

    }

    private fun bindUiState(uiState: MainViewModel.UiState?) {

        binding.resultado.text = getString(R.string.resultado_kcal, uiState?.tmb)
        binding.ganhoPeso.text = getString(R.string.resultado_kcal, uiState?.ganhoPeso)
        binding.hipertrofia.text = getString(R.string.resultado_kcal, uiState?.hipertrofia)
        binding.emagrecimento.text = getString(R.string.resultado_kcal, uiState?.emagrecimento)

        if (uiState?.resultadoVisivel == true)
            binding.resultadoLayout.visibility = View.VISIBLE
        else
            binding.resultadoLayout.visibility = View.INVISIBLE
    }

    private fun exibirMensagemInicial() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        val showAbout = sharedPref.getBoolean("showAbout", false)

        if (!showAbout) {
            showAboutDialog()
            sharedPref.edit().putBoolean("showAbout", true).apply()
        }
    }

    private fun showAboutDialog() {
        val sobreFragment = SobreFragment()
        sobreFragment.show(supportFragmentManager, "sobreFragment")
    }

    private fun validarCampos(
        idade: String,
        peso: String,
        altura: String
    ): Boolean {

        if (idade.isBlank())
            binding.idadeEditText.error = getString(R.string.idade_invalida)

        if (peso.isBlank())
            binding.pesoEditText.error = getString(R.string.peso_invalido)

        if (altura.isBlank())
            binding.alturaEditText.error = getString(R.string.altura_invalida)

        return (idade.isBlank() || peso.isBlank() || altura.isBlank())
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}