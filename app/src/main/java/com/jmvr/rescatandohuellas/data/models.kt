package com.jmvr.rescatandohuellas.data

enum class TipoMascota { PERRO, GATO, OTRO }

enum class EstadoMascota { EN_BUSQUEDA, AVISTADA, EN_REFUGIO, REENCONTRADA }

enum class TipoReporte { PERDIDA, ENCONTRADA, RIESGO }

data class Mascota(
    val nombre: String,
    val tipo: TipoMascota,
    val senas: String,
    val estado: EstadoMascota,
    val ubicacion: String,
    val tiempo: String,
    val distanciaKm: Double? = null
)

data class Avistamiento(
    val titulo: String,
    val subtitulo: String,
    val tiempo: String
)

data class PuntoAyuda(
    val nombre: String,
    val tipo: String,
    val distanciaKm: Double,
    val detalle: String
)

data class Estadisticas(
    val reportadas: Int,
    val reencontradas: Int,
    val voluntarios: Int
)

data class EstadoZona(
    val emergenciaActiva: Boolean,
    val nombreEmergencia: String,
    val zona: String,
    val tiempoActivacion: String
)
