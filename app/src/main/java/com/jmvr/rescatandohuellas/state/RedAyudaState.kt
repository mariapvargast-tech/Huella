package com.jmvr.rescatandohuellas.state

import com.jmvr.rescatandohuellas.data.CategoriaAyuda
import com.jmvr.rescatandohuellas.data.RecursoAyuda

/** Filtra por categoría y por texto (en nombre o detalle, sin distinguir mayúsculas), del más cercano al más lejano. */
fun filtrarRecursosAyuda(
    recursos: List<RecursoAyuda>,
    categoria: CategoriaAyuda,
    busqueda: String
): List<RecursoAyuda> {
    val texto = busqueda.trim()
    return recursos
        .filter { it.categoria == categoria }
        .filter { texto.isEmpty() || it.nombre.contains(texto, ignoreCase = true) || it.detalle.contains(texto, ignoreCase = true) }
        .sortedBy { it.distanciaKm }
}
