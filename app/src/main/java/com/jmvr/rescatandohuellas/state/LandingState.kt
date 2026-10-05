package com.jmvr.rescatandohuellas.state

const val URL_LANDING_POR_DEFECTO = "https://mariapvargast-tech.github.io/Huella/"
const val URL_LANDING_OFFLINE = "file:///android_asset/landing.html"

fun normalizarUrl(texto: String): String? {
    val limpio = texto.trim()
    if (limpio.isEmpty() || limpio.any { it.isWhitespace() }) return null
    return if ("://" in limpio) limpio else "https://$limpio"
}
