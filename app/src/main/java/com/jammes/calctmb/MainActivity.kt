package com.jammes.calctmb

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jammes.calctmb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.Q)
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

        binding.resultadoLayout.visibility = View.INVISIBLE

        binding.calcularButton.setOnClickListener {
            val idade = binding.idadeEditText.text.toString()
            val peso = binding.pesoEditText.text.toString()
            val altura = binding.alturaEditText.text.toString()
            val sexo = binding.chipsSexo.checkedChipId

            if (validarCampos(idade, peso, altura))
                return@setOnClickListener

            val resultado = if (sexo == R.id.chipMasculino) {
                88.362 + (13.397 * peso.toDouble()) + (4.799 * altura.toDouble()) - (5.677 * idade.toDouble())
            } else {
                447.593 + (9.247 * peso.toDouble()) + (3.098 * altura.toDouble()) - (4.330 * idade.toDouble())
            }

            val tmb = "%.2f".format(resultado)
            val ganho = "%.2f".format(resultado * 1.4)
            val hipertrofia = "%.2f".format(resultado * 0.8)
            val emagrecimento = "%.2f".format(resultado * 1.2)

            binding.resultadoLayout.visibility = View.VISIBLE

            binding.resultado.text = getString(R.string.resultado_kcal, tmb)
            binding.ganhoPeso.text = getString(R.string.resultado_kcal, ganho)
            binding.hipertrofia.text = getString(R.string.resultado_kcal, hipertrofia)
            binding.emagrecimento.text = getString(R.string.resultado_kcal, emagrecimento)

            hideKeyboard()

            binding.mainScroll.fullScroll(View.FOCUS_DOWN)
        }

        binding.sobreButton.setOnClickListener {
            showAboutDialog()
        }

        exibirMensagemInicial()

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}