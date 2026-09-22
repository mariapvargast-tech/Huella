package com.jmvr.rescatandohuellas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jmvr.rescatandohuellas.ui.theme.RescatandoHuellasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RescatandoHuellasTheme {
                HuellaApp()
            }
        }
    }
}
