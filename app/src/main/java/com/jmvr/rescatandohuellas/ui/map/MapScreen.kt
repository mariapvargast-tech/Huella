package com.jmvr.rescatandohuellas.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Red de ayuda cercana", style = MaterialTheme.typography.titleLarge)
                Text(
                    "El mapa interactivo llega en la próxima entrega. Por ahora, esta es la lista de puntos de ayuda.",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        items(SampleData.puntosAyuda) { punto ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(punto.nombre, fontWeight = FontWeight.Bold)
                    Text(
                        "${punto.tipo} · ${punto.distanciaKm} km",
                        color = HuellaOnSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(punto.detalle, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
