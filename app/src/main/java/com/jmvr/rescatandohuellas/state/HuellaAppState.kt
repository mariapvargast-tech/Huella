package com.jmvr.rescatandohuellas.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jmvr.rescatandohuellas.navigation.HuellaDestination

class HuellaAppState {
    var destino by mutableStateOf(HuellaDestination.RESCATES)
        private set
    var emergenciaActiva by mutableStateOf(true)

    fun irA(destino: HuellaDestination) {
        this.destino = destino
    }
}

@Composable
fun rememberHuellaAppState(): HuellaAppState = remember { HuellaAppState() }
