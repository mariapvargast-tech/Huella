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
    var mostrandoAcercaDe by mutableStateOf(false)
        private set
    var mostrandoRedAyuda by mutableStateOf(false)
        private set
    var urlLanding by mutableStateOf(URL_LANDING_POR_DEFECTO)

    var perfil by mutableStateOf(SampleData.perfil)

    fun irA(destino: HuellaDestination) {
        this.destino = destino
        this.reportando = false
        this.mostrandoAcercaDe = false
        this.mostrandoRedAyuda = false
    }

    fun abrirReportar() {
        reportando = true
        mostrandoAcercaDe = false
        mostrandoRedAyuda = false
    }

    fun cerrarReportar() {
        reportando = false
    }

    fun abrirAcercaDe() {
        mostrandoAcercaDe = true
        reportando = false
        mostrandoRedAyuda = false
    }

    fun abrirRedAyuda() {
        mostrandoRedAyuda = true
        reportando = false
        mostrandoAcercaDe = false
    }

    fun cerrarRedAyuda() {
        mostrandoRedAyuda = false
    }

    fun cerrarAcercaDe() {
        mostrandoAcercaDe = false
    }
}

@Composable
fun rememberHuellaAppState(): HuellaAppState = remember { HuellaAppState() }
