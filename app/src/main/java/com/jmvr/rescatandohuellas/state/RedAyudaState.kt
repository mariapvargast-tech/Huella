package com.jmvr.rescatandohuellas.state

import com.jmvr.rescatandohuellas.data.CategoriaAyuda
import com.jmvr.rescatandohuellas.data.RecursoAyuda
import java.text.Normalizer

private val MARCAS_DE_TILDE = Regex("\\p{Mn}+")

private fun sinTildes(texto: String): String =
    MARCAS_DE_TILDE.replace(Normalizer.normalize(texto, Normalizer.Form.NFD), "")

/** Filtra por categoría y por texto (en nombre o detalle, sin distinguir mayúsculas ni tildes), del más cercano al más lejano. */
fun filtrarRecursosAyuda(
    recursos: List<RecursoAyuda>,
    categoria: CategoriaAyuda,
    busqueda: String
): List<RecursoAyuda> {
    val texto = sinTildes(busqueda.trim())
    return recursos
        .filter { it.categoria == categoria }
        .filter { texto.isEmpty() || sinTildes(it.nombre).contains(texto, ignoreCase = true) || sinTildes(it.detalle).contains(texto, ignoreCase = true) }
        .sortedBy { it.distanciaKm }
}
