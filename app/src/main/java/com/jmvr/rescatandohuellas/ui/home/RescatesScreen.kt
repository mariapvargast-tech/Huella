package com.jmvr.rescatandohuellas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.Avistamiento
import com.jmvr.rescatandohuellas.data.Mascota
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoral
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreen
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreenTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrange

@Composable
fun RescatesScreen(
    emergenciaActiva: Boolean,
    onIrAMapa: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            EmergenciaBanner(emergenciaActiva)
        }

        item {
            Text("Acciones rápidas", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        item {
            AccionesRapidas(onIrAMapa)
        }

        item {
            Text("Casos cercanos", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        items(SampleData.casosCercanos) { mascota ->
            CasoCercanoCard(mascota)
        }

        item {
            Text("Avistamientos recientes", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        items(SampleData.avistamientosRecientes) { avistamiento ->
            AvistamientoItem(avistamiento)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp)
            ) {
                EstadisticaColumna("reportadas", SampleData.estadisticas.reportadas, Modifier.weight(1f))
                EstadisticaColumna("reencontradas", SampleData.estadisticas.reencontradas, Modifier.weight(1f))
                EstadisticaColumna("voluntarios", SampleData.estadisticas.voluntarios, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EmergenciaBanner(emergenciaActiva: Boolean) {
    if (emergenciaActiva) {
        Card(
            colors = CardDefaults.cardColors(containerColor = HuellaCoral),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "EMERGENCIA ACTIVA · ${SampleData.estadoZona.tiempoActivacion.uppercase()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    SampleData.estadoZona.nombreEmergencia,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Tu zona (${SampleData.estadoZona.zona}) está dentro del área afectada",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    } else {
        Card(
            colors = CardDefaults.cardColors(containerColor = HuellaGreenTint),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Todo tranquilo en tu zona", fontWeight = FontWeight.Bold, color = HuellaGreen)
                Text(
                    "Sin alertas activas · ${SampleData.casosCercanos.size} reportes abiertos cerca",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun AccionesRapidas(onIrAMapa: () -> Unit) {
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { context.mostrarProximaEntrega() },
            colors = ButtonDefaults.buttonColors(containerColor = HuellaOrange),
            modifier = Modifier.weight(1f)
        ) { Text("Reportar") }
        Button(
            onClick = onIrAMapa,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.weight(1f)
        ) { Text("Ver mapa") }
        Button(
            onClick = { context.mostrarProximaEntrega() },
            colors = ButtonDefaults.buttonColors(containerColor = HuellaCoral),
            modifier = Modifier.weight(1f)
        ) { Text("SOS") }
    }
}

@Composable
private fun CasoCercanoCard(mascota: Mascota) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(mascota.nombre, fontWeight = FontWeight.Bold)
            Text(mascota.senas, color = HuellaOnSurfaceMuted, style = MaterialTheme.typography.bodySmall)
            Text(
                "${mascota.ubicacion} · ${mascota.tiempo}",
                color = HuellaOnSurfaceMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AvistamientoItem(avistamiento: Avistamiento) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(avistamiento.titulo, fontWeight = FontWeight.Medium)
        Text(
            "${avistamiento.subtitulo} · ${avistamiento.tiempo}",
            color = HuellaOnSurfaceMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun EstadisticaColumna(etiqueta: String, valor: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(etiqueta, color = HuellaOnSurfaceMuted, style = MaterialTheme.typography.labelSmall)
    }
}
