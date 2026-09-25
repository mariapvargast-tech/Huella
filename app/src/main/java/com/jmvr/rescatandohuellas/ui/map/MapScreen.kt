package com.jmvr.rescatandohuellas.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.PuntoAyuda
import com.jmvr.rescatandohuellas.data.ReporteMapa
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.state.MapaReportesManager
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

private data class PuntoSimulado(val latitud: Double, val longitud: Double)

private fun etiquetaDeReporte(tipo: TipoReporte) = when (tipo) {
    TipoReporte.PERDIDA -> "Perdidas"
    TipoReporte.ENCONTRADA -> "Encontradas"
    TipoReporte.RIESGO -> "En riesgo"
}

private fun nombreDeReporte(tipo: TipoReporte) = when (tipo) {
    TipoReporte.PERDIDA -> "Mascota perdida"
    TipoReporte.ENCONTRADA -> "Animal encontrado"
    TipoReporte.RIESGO -> "Animal en riesgo"
}

@Composable
fun MapScreen(mapa: MapaReportesManager, modifier: Modifier = Modifier) {
    var puntoSimulado by remember { mutableStateOf<PuntoSimulado?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)) {
            Text("Reportes cercanos", style = MaterialTheme.typography.titleLarge)
            Text(
                "Toca un pin para ver el caso. Mantén presionado el mapa para simular un reporte.",
                color = HuellaOnSurfaceMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
        FiltrosReportes(
            filtro = mapa.filtro,
            onFiltroChange = { mapa.cambiarFiltro(it) }
        )
        ReportesMapa(
            reportes = mapa.visibles,
            seleccionadoId = mapa.seleccionado?.id,
            onSeleccionar = { mapa.seleccionar(it) },
            onMantenerPresionado = { lat, lng -> puntoSimulado = PuntoSimulado(lat, lng) },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        val seleccionado = mapa.seleccionado
        if (seleccionado != null) {
            DetalleReporte(reporte = seleccionado, onCerrar = { mapa.seleccionar(null) })
        } else {
            PuntosAyuda(puntos = SampleData.puntosAyuda)
        }
    }

    puntoSimulado?.let { punto ->
        DialogoSimularReporte(
            onElegir = { tipo ->
                mapa.simularReporte(tipo, punto.latitud, punto.longitud)
                puntoSimulado = null
            },
            onCancelar = { puntoSimulado = null }
        )
    }
}

@Composable
private fun FiltrosReportes(filtro: TipoReporte?, onFiltroChange: (TipoReporte?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = filtro == null,
            onClick = { onFiltroChange(null) },
            label = { Text("Todos") }
        )
        TipoReporte.entries.forEach { tipo ->
            FilterChip(
                selected = filtro == tipo,
                onClick = { onFiltroChange(tipo) },
                label = { Text(etiquetaDeReporte(tipo)) },
                leadingIcon = { PuntoColor(colorDeReporte(tipo)) }
            )
        }
    }
}

@Composable
private fun PuntoColor(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun DetalleReporte(reporte: ReporteMapa, onCerrar: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PuntoColor(colorDeReporte(reporte.tipo))
                Text(
                    "${nombreDeReporte(reporte.tipo)} · ${reporte.tiempo}",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(reporte.titulo, style = MaterialTheme.typography.titleMedium)
            Text(reporte.detalle, style = MaterialTheme.typography.bodySmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCerrar) { Text("Cerrar") }
                TextButton(onClick = { context.mostrarProximaEntrega() }) { Text("Ver caso") }
            }
        }
    }
}

@Composable
private fun PuntosAyuda(puntos: List<PuntoAyuda>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            "Red de ayuda cercana",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(puntos) { punto ->
                Card(
                    modifier = Modifier.width(200.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
}

@Composable
private fun DialogoSimularReporte(onElegir: (TipoReporte) -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Simular reporte aquí") },
        text = {
            Column {
                TipoReporte.entries.forEach { tipo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onElegir(tipo) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PuntoColor(colorDeReporte(tipo))
                        Text(nombreDeReporte(tipo), modifier = Modifier.padding(start = 10.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
