package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.data.CategoriaAyuda
import com.jmvr.rescatandohuellas.data.RecursoAyuda
import com.jmvr.rescatandohuellas.state.filtrarRecursosAyuda
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RedAyudaStateTest {
    private val lejano = RecursoAyuda(1, "Refugio Lejano", CategoriaAyuda.REFUGIO, 5.0, false, "Sin disponibilidad")
    private val cercano = RecursoAyuda(2, "Refugio Cercano", CategoriaAyuda.REFUGIO, 1.0, true, "4 espacios disponibles")
    private val vet = RecursoAyuda(3, "Clínica Sur", CategoriaAyuda.VETERINARIA, 0.5, true, "Atiende emergencias")
    private val todos = listOf(lejano, cercano, vet)

    @Test
    fun `solo deja la categoria elegida, del mas cercano al mas lejano`() {
        assertEquals(listOf(cercano, lejano), filtrarRecursosAyuda(todos, CategoriaAyuda.REFUGIO, ""))
    }

    @Test
    fun `busca en nombre y detalle sin distinguir mayusculas`() {
        assertEquals(listOf(cercano), filtrarRecursosAyuda(todos, CategoriaAyuda.REFUGIO, "  CERCANO "))
        assertEquals(listOf(lejano), filtrarRecursosAyuda(todos, CategoriaAyuda.REFUGIO, "sin disp"))
    }

    @Test
    fun `la busqueda ignora las tildes en ambos sentidos`() {
        assertEquals(listOf(vet), filtrarRecursosAyuda(todos, CategoriaAyuda.VETERINARIA, "clinica"))
        assertEquals(listOf(vet), filtrarRecursosAyuda(todos, CategoriaAyuda.VETERINARIA, "CLÍNICA"))
    }

    @Test
    fun `sin coincidencias devuelve lista vacia`() {
        assertTrue(filtrarRecursosAyuda(todos, CategoriaAyuda.VOLUNTARIO, "").isEmpty())
    }
}
