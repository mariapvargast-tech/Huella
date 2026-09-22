package com.jmvr.rescatandohuellas.data

object SampleData {
    val estadoZona = EstadoZona(
        emergenciaActiva = true,
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

    val puntosAyuda = listOf(
        PuntoAyuda("Clínica San Joaquín", "Veterinaria", 1.2, "Atiende heridos ahora"),
        PuntoAyuda("Hogar de paso Laureles", "Refugio", 2.4, "12 cupos disponibles"),
        PuntoAyuda("Andrés", "Voluntario", 0.8, "Transporte en moto")
    )
}
