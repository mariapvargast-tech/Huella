package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.ui.components.PulgarBarra
import com.jmvr.rescatandohuellas.ui.components.calcularPulgarBarra
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BarraDesplazamientoTest {
    @Test
    fun `sin barra cuando el contenido cabe`() {
        assertNull(calcularPulgarBarra(visible = 100f, contenido = 100f, desplazado = 0f))
        assertNull(calcularPulgarBarra(visible = 100f, contenido = 60f, desplazado = 0f))
    }

    @Test
    fun `el alto del pulgar es proporcional a lo visible`() {
        // Se ve la mitad del contenido -> el pulgar ocupa la mitad de la barra.
        assertEquals(PulgarBarra(inicio = 0f, alto = 50f), calcularPulgarBarra(100f, 200f, 0f))
    }

    @Test
    fun `el pulgar baja segun lo desplazado`() {
        assertEquals(25f, calcularPulgarBarra(100f, 200f, 50f)!!.inicio, 0.001f)
        assertEquals(50f, calcularPulgarBarra(100f, 200f, 100f)!!.inicio, 0.001f)
        // Nunca se sale de la barra.
        assertEquals(50f, calcularPulgarBarra(100f, 200f, 500f)!!.inicio, 0.001f)
    }

    @Test
    fun `el pulgar tiene un alto minimo con contenido muy largo`() {
        assertEquals(24f, calcularPulgarBarra(100f, 100_000f, 0f)!!.alto, 0.001f)
    }
}
