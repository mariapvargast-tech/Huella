package com.jmvr.rescatandohuellas.data

import com.jmvr.rescatandohuellas.R

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

    val perfil = PerfilPersona(
        nombre = "María Paula Vargas",
        rol = "Voluntaria activa · Laureles, Medellín",
        foto = R.drawable.perfil_persona,
        acercaDe = "Me uní a Rescatando Huellas para ayudar a que cada mascota perdida vuelva a casa. " +
            "Coordino avistamientos en Laureles y apoyo el transporte de animales heridos hacia " +
            "veterinarias aliadas. Creo en la adopción responsable y en el trabajo en red: " +
            "cuando la comunidad se organiza, los rescates son más rápidos y seguros. " +
            "En mis tiempos libres acompaño jornadas de esterilización y ferias de adopción.",
        estudios = "Tecnología en Desarrollo de Software — en curso.\n\n" +
            "Curso de primeros auxilios veterinarios básicos — Fundación Huellitas, 2025.\n\n" +
            "Diplomado en bienestar animal y tenencia responsable — 2024.\n\n" +
            "Bachiller académico — Institución Educativa Laureles, 2022.",
        experiencia = "Voluntaria de rescate — Rescatando Huellas (2025 – hoy). " +
            "Atención de reportes de mascotas perdidas y animales en riesgo en Laureles y Belén.\n\n" +
            "Apoyo en hogar de paso — Hogar de paso Laureles (2024 – 2025). " +
            "Cuidado temporal de perros y gatos rescatados mientras encontraban familia.\n\n" +
            "Logística en ferias de adopción — 2023 – 2024. " +
            "Organización de turnos, registro de adoptantes y seguimiento posadopción."
    )

    val fotosAdopcion = listOf(
        FotoGaleria(1, R.drawable.adopcion_perro_canela, "Canela", "Perrita mestiza de 2 años, de pelo rizado color cobre. Dulce, tranquila y acostumbrada a pasear con correa."),
        FotoGaleria(2, R.drawable.adopcion_perro_toby, "Toby", "Perro pequeño de 3 años, peludo y muy cariñoso. Ideal para apartamento; se lleva bien con niños."),
        FotoGaleria(3, R.drawable.adopcion_perro_bruno, "Bruno", "Perro adulto mayor de 10 años, rescatado en las inundaciones de Laureles. Calmado y leal; busca un hogar tranquilo para sus últimos años."),
        FotoGaleria(4, R.drawable.adopcion_gato_oreo, "Oreo", "Gato blanco y negro de 1 año. Juguetón, le encanta dormir al sol y ya está esterilizado."),
        FotoGaleria(5, R.drawable.adopcion_gato_sombra, "Sombra", "Gata negra de 2 años, de ojos amarillos. Independiente pero cariñosa cuando toma confianza."),
        FotoGaleria(6, R.drawable.adopcion_gato_pelusa, "Pelusa", "Gatito de 2 meses rescatado en Belén. Necesita un hogar paciente y sus primeras vacunas al día.")
    )
}
