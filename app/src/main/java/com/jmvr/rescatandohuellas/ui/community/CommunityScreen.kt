package com.jmvr.rescatandohuellas.ui.community

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
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun CommunityScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Comunidad", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { context.mostrarProximaEntrega() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${SampleData.estadisticas.voluntarios} voluntarios activos")
                Text(
                    "${SampleData.puntosAyuda.count { it.tipo == "Refugio" }} refugios cerca",
                    color = HuellaOnSurfaceMuted
                )
                Text(
                    "Feed de historias y guías disponible en la próxima entrega",
                    color = HuellaOnSurfaceMuted
                )
            }
        }
    }
}
