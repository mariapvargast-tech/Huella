package com.jmvr.rescatandohuellas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector

enum class HuellaDestination(val etiqueta: String, val icono: ImageVector) {
    RESCATES("Rescates", Icons.Default.Home),
    MAPA("Mapa", Icons.Default.LocationOn),
    ADOPCION("Adopción", Icons.Default.Pets),
    COMUNIDAD("Comunidad", Icons.Default.Group),
    PERFIL("Perfil", Icons.Default.Person)
}
