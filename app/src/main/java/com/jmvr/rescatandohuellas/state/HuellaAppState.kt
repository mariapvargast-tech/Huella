package com.jmvr.rescatandohuellas.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.navigation.HuellaDestination

class HuellaAppState {
    var destino by mutableStateOf(HuellaDestination.RESCATES)
        private set
    var emergenciaActiva by mutableStateOf(true)
    var reportando by mutableStateOf(false)
        private set
    val mapaReportes = MapaReportesManager(SampleData.reportesMapa)

    fun irA(destino: HuellaDestination) {
        this.destino = destino
        this.reportando = false
    }

    fun abrirReportar() {
        reportando = true
    }

    fun cerrarReportar() {
        reportando = false
    }
}

@Composable
fun rememberHuellaAppState(): HuellaAppState = remember { HuellaAppState() }
