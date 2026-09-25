package com.jmvr.rescatandohuellas.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jmvr.rescatandohuellas.data.ReporteMapa
import com.jmvr.rescatandohuellas.data.TipoReporte
import java.util.Locale

/** @param filtro null muestra todos los tipos. */
fun filtrarReportes(reportes: List<ReporteMapa>, filtro: TipoReporte?): List<ReporteMapa> =
    if (filtro == null) reportes else reportes.filter { it.tipo == filtro }

fun siguienteIdReporte(reportes: List<ReporteMapa>): Int = (reportes.maxOfOrNull { it.id } ?: 0) + 1

fun crearReporteSimulado(id: Int, tipo: TipoReporte, latitud: Double, longitud: Double): ReporteMapa =
    ReporteMapa(
        id = id,
        tipo = tipo,
        titulo = when (tipo) {
            TipoReporte.PERDIDA -> "Mascota perdida"
            TipoReporte.ENCONTRADA -> "Animal encontrado"
            TipoReporte.RIESGO -> "Animal en riesgo"
        },
        detalle = String.format(Locale.US, "%.5f, %.5f", latitud, longitud),
        latitud = latitud,
        longitud = longitud,
        tiempo = "ahora"
    )

class MapaReportesManager(iniciales: List<ReporteMapa>) {
    val reportes = mutableStateListOf<ReporteMapa>().apply { addAll(iniciales) }
    var filtro by mutableStateOf<TipoReporte?>(null)
        private set
    var seleccionadoId by mutableStateOf<Int?>(null)
        private set

    val visibles: List<ReporteMapa> get() = filtrarReportes(reportes, filtro)
    val seleccionado: ReporteMapa? get() = visibles.firstOrNull { it.id == seleccionadoId }

    fun cambiarFiltro(nuevo: TipoReporte?) {
        filtro = nuevo
    }

    fun seleccionar(id: Int?) {
        seleccionadoId = id
    }

    fun simularReporte(tipo: TipoReporte, latitud: Double, longitud: Double) {
        val nuevo = crearReporteSimulado(siguienteIdReporte(reportes), tipo, latitud, longitud)
        reportes.add(nuevo)
        if (filtro != null && filtro != tipo) filtro = null
        seleccionadoId = nuevo.id
    }
}
