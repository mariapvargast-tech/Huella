package com.jmvr.rescatandohuellas.state

import com.jmvr.rescatandohuellas.data.PerfilPersona

data class EdicionPerfil(
    val nombre: String,
    val rol: String,
    val acercaDe: String,
    val estudios: String,
    val experiencia: String
)

fun PerfilPersona.aEdicion() = EdicionPerfil(nombre, rol, acercaDe, estudios, experiencia)

fun EdicionPerfil.esValida(): Boolean = nombre.isNotBlank()

fun PerfilPersona.aplicar(edicion: EdicionPerfil): PerfilPersona? {
    if (!edicion.esValida()) return null
    return copy(
        nombre = edicion.nombre.trim(),
        rol = edicion.rol.trim(),
        acercaDe = edicion.acercaDe.trim(),
        estudios = edicion.estudios.trim(),
        experiencia = edicion.experiencia.trim()
    )
}
