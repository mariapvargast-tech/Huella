package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.state.PASO_CONFIRMACION
import com.jmvr.rescatandohuellas.state.PASO_DATOS
import com.jmvr.rescatandohuellas.state.PASO_TIPO
import com.jmvr.rescatandohuellas.state.ReporteForm
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeDatos
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeTipo
import com.jmvr.rescatandohuellas.state.siguientePaso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportFlowStateTest {
    @Test
    fun `no se puede avanzar del paso tipo sin elegir tipo`() {
        assertFalse(puedeAvanzarDesdeTipo(ReporteForm()))
        assertTrue(puedeAvanzarDesdeTipo(ReporteForm(tipo = TipoReporte.PERDIDA)))
    }

    @Test
    fun `no se puede avanzar del paso datos sin nombre o ubicacion`() {
        val base = ReporteForm(tipo = TipoReporte.PERDIDA)
        assertFalse(puedeAvanzarDesdeDatos(base))
        assertFalse(puedeAvanzarDesdeDatos(base.copy(nombre = "Luna")))
        assertTrue(puedeAvanzarDesdeDatos(base.copy(nombre = "Luna", ubicacion = "Laureles")))
    }

    @Test
    fun `siguientePaso solo avanza cuando el paso actual es valido`() {
        assertEquals(PASO_TIPO, siguientePaso(PASO_TIPO, ReporteForm()))

        val conTipo = ReporteForm(tipo = TipoReporte.ENCONTRADA)
        assertEquals(PASO_DATOS, siguientePaso(PASO_TIPO, conTipo))

        val datosIncompletos = conTipo.copy(nombre = "Rocky")
        assertEquals(PASO_DATOS, siguientePaso(PASO_DATOS, datosIncompletos))

        val datosCompletos = conTipo.copy(nombre = "Rocky", ubicacion = "Belén")
        assertEquals(PASO_CONFIRMACION, siguientePaso(PASO_DATOS, datosCompletos))

        assertEquals(PASO_CONFIRMACION, siguientePaso(PASO_CONFIRMACION, datosCompletos))
    }
}
