package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.data.ReporteMapa
import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.state.crearReporteSimulado
import com.jmvr.rescatandohuellas.state.filtrarReportes
import com.jmvr.rescatandohuellas.state.siguienteIdReporte
import org.junit.Assert.assertEquals
import org.junit.Test

class MapaReportesStateTest {
    private fun reporte(id: Int, tipo: TipoReporte) =
        ReporteMapa(id, tipo, "R$id", "", 6.24, -75.59, "ahora")

    private val reportes = listOf(
        reporte(1, TipoReporte.PERDIDA),
        reporte(2, TipoReporte.RIESGO),
        reporte(5, TipoReporte.PERDIDA)
    )

    @Test
    fun `sin filtro se muestran todos los reportes`() {
        assertEquals(reportes, filtrarReportes(reportes, null))
    }

    @Test
    fun `el filtro deja solo los reportes de ese tipo`() {
        assertEquals(listOf(1, 5), filtrarReportes(reportes, TipoReporte.PERDIDA).map { it.id })
        assertEquals(emptyList<Int>(), filtrarReportes(reportes, TipoReporte.ENCONTRADA).map { it.id })
    }

    @Test
    fun `el siguiente id es el mayor mas uno`() {
        assertEquals(6, siguienteIdReporte(reportes))
        assertEquals(1, siguienteIdReporte(emptyList()))
    }

    @Test
    fun `el reporte simulado conserva tipo y coordenadas`() {
        val simulado = crearReporteSimulado(7, TipoReporte.RIESGO, 6.25, -75.58)
        assertEquals(7, simulado.id)
        assertEquals(TipoReporte.RIESGO, simulado.tipo)
        assertEquals(6.25, simulado.latitud, 0.0)
        assertEquals(-75.58, simulado.longitud, 0.0)
        assertEquals("6.25000, -75.58000", simulado.detalle)
    }
}
