package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.data.PerfilPersona
import com.jmvr.rescatandohuellas.state.aEdicion
import com.jmvr.rescatandohuellas.state.aplicar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PerfilStateTest {
    private val perfil = PerfilPersona(
        nombre = "Ana",
        rol = "Voluntaria",
        foto = 42,
        acercaDe = "Hola",
        estudios = "Ninguno",
        experiencia = "Poca"
    )

    @Test
    fun `sin cambios el perfil queda igual`() {
        assertEquals(perfil, perfil.aplicar(perfil.aEdicion()))
    }

    @Test
    fun `aplica los cambios, quita espacios sobrantes y conserva la foto`() {
        val editado = perfil.aplicar(perfil.aEdicion().copy(nombre = "  Ana María ", estudios = "Tecnología\n"))
        assertEquals("Ana María", editado!!.nombre)
        assertEquals("Tecnología", editado.estudios)
        assertEquals(42, editado.foto)
    }

    @Test
    fun `el nombre no puede quedar vacio`() {
        assertNull(perfil.aplicar(perfil.aEdicion().copy(nombre = "   ")))
    }
}
