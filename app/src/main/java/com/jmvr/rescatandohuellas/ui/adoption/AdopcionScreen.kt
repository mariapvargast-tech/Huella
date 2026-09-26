package com.jmvr.rescatandohuellas.ui.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

private data class AnimalEnAdopcion(val nombre: String, val tipo: String, val edad: String)

private val animalesDeEjemplo = listOf(
    AnimalEnAdopcion("Milo", "Perro mestizo", "2 años"),
    AnimalEnAdopcion("Cleo", "Gata siamés", "1 año")
)

@Composable
fun AdopcionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Adopción", style = MaterialTheme.typography.titleLarge)
        Text(
            "Publicar, filtrar y adoptar llega en la próxima entrega.",
            color = HuellaOnSurfaceMuted,
            style = MaterialTheme.typography.bodySmall
        )
        animalesDeEjemplo.forEach { animal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { context.mostrarProximaEntrega() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(animal.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("${animal.tipo} · ${animal.edad}", color = HuellaOnSurfaceMuted)
                }
            }
        }
    }
}
