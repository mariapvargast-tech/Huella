package com.jmvr.rescatandohuellas.data

object SampleData {
    val estadoZona = EstadoZona(
        nombreEmergencia = "Inundaciones — Valle de Aburrá",
        zona = "Laureles",
        tiempoActivacion = "hace 5 min"
    )

    val estadisticas = Estadisticas(reportadas = 127, reencontradas = 43, voluntarios = 18)

    val casosCercanos = listOf(
        Mascota(
            nombre = "Gato herido",
            tipo = TipoMascota.GATO,
            senas = "Bajo un carro, Av. 33",
            estado = EstadoMascota.AVISTADA,
            ubicacion = "Av. 33 con 74",
            tiempo = "hace 8 min",
            distanciaKm = 0.7
        ),
        Mascota(
            nombre = "Rocky",
            tipo = TipoMascota.PERRO,
            senas = "Golden, collar azul",
            estado = EstadoMascota.EN_BUSQUEDA,
            ubicacion = "Laureles",
            tiempo = "hace 25 min",
            distanciaKm = 1.2
        ),
        Mascota(
            nombre = "Nube",
            tipo = TipoMascota.GATO,
            senas = "Blanca, sin collar",
            estado = EstadoMascota.REENCONTRADA,
            ubicacion = "Belén",
            tiempo = "hace 1 h",
            distanciaKm = null
        )
    )

    val avistamientosRecientes = listOf(
        Avistamiento("Avistamiento: golden con collar azul", "Coincide con \"Rocky\"", "hace 25 min"),
        Avistamiento("Nube volvió con su familia", "Reportada ayer en Belén", "hace 1 h")
    )

    val reportesMapa = listOf(
        ReporteMapa(
            id = 1,
            tipo = TipoReporte.RIESGO,
            titulo = "Gato herido",
            detalle = "Bajo un carro, Av. 33 con 74",
            latitud = 6.2447,
            longitud = -75.5905,
            tiempo = "hace 8 min"
        ),
        ReporteMapa(
            id = 2,
            tipo = TipoReporte.PERDIDA,
            titulo = "Rocky",
            detalle = "Golden, collar azul — visto por última vez en Laureles",
            latitud = 6.2455,
            longitud = -75.5960,
            tiempo = "hace 25 min"
        ),
        ReporteMapa(
            id = 3,
            tipo = TipoReporte.ENCONTRADA,
            titulo = "Perrita criolla",
            detalle = "Café con manchas blancas, está en un hogar de paso",
            latitud = 6.2528,
            longitud = -75.5880,
            tiempo = "hace 40 min"
        ),
        ReporteMapa(
            id = 4,
            tipo = TipoReporte.PERDIDA,
            titulo = "Michi",
            detalle = "Gato gris atigrado, collar rojo",
            latitud = 6.2310,
            longitud = -75.6040,
            tiempo = "hace 1 h"
        ),
        ReporteMapa(
            id = 5,
            tipo = TipoReporte.RIESGO,
            titulo = "Perro atrapado",
            detalle = "Atrapado por el agua cerca de la quebrada",
            latitud = 6.2390,
            longitud = -75.5820,
            tiempo = "hace 1 h"
        ),
        ReporteMapa(
            id = 6,
            tipo = TipoReporte.ENCONTRADA,
            titulo = "Loro encontrado",
            detalle = "Verde, muy manso, en la estación Estadio",
            latitud = 6.2530,
            longitud = -75.5760,
            tiempo = "hace 2 h"
        )
    )

    val puntosAyuda = listOf(
        PuntoAyuda("Clínica San Joaquín", "Veterinaria", 1.2, "Atiende heridos ahora"),
        PuntoAyuda("Hogar de paso Laureles", "Refugio", 2.4, "12 cupos disponibles"),
        PuntoAyuda("Andrés", "Voluntario", 0.8, "Transporte en moto")
    )
}
