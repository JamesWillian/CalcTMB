package com.jammes.calctmb

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jammes.calctmb.ui.calculator.CalculatorRoute
import com.jammes.calctmb.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            // A barra superior é navy nos dois temas, então os ícones de status
            // precisam ser claros sempre.
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorRoute()
                }
            }
        }
    }
}
