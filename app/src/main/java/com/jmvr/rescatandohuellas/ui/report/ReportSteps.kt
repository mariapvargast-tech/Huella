package com.jmvr.rescatandohuellas.ui.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.TipoMascota
import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.state.ReporteForm
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoral
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoralTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrangeTint

@Composable
fun PasoTipo(form: ReporteForm, onFormChange: (ReporteForm) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OpcionTipo(
            titulo = "Mascota perdida",
            descripcion = "Se extravió y necesito ayuda para encontrarla",
            seleccionado = form.tipo == TipoReporte.PERDIDA,
            colorFondo = HuellaOrangeTint,
            onClick = { onFormChange(form.copy(tipo = TipoReporte.PERDIDA)) }
        )
        OpcionTipo(
            titulo = "Animal encontrado",
            descripcion = "Está conmigo o lo vi hace poco",
            seleccionado = form.tipo == TipoReporte.ENCONTRADA,
            colorFondo = HuellaOrangeTint,
            onClick = { onFormChange(form.copy(tipo = TipoReporte.ENCONTRADA)) }
        )
        OpcionTipo(
            titulo = "Animal en situación de riesgo",
            descripcion = "Herido, atrapado o en peligro — se avisa primero a veterinarios",
            seleccionado = form.tipo == TipoReporte.RIESGO,
            colorFondo = HuellaCoralTint,
            onClick = { onFormChange(form.copy(tipo = TipoReporte.RIESGO)) }
        )
    }
}

@Composable
private fun OpcionTipo(
    titulo: String,
    descripcion: String,
    seleccionado: Boolean,
    colorFondo: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) colorFondo else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PasoDatos(form: ReporteForm, onFormChange: (ReporteForm) -> Unit, modifier: Modifier = Modifier) {
    val esRiesgo = form.tipo == TipoReporte.RIESGO
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (esRiesgo) {
            Text(
                "Prioridad alta: se avisa primero a veterinarios cercanos",
                color = HuellaCoral,
                style = MaterialTheme.typography.labelLarge
            )
        }
        OutlinedTextField(
            value = form.nombre,
            onValueChange = { onFormChange(form.copy(nombre = it)) },
            label = { Text("Nombre (si lo sabes)") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Tipo de mascota", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            TipoMascota.entries.forEach { tipo ->
                val seleccionado = form.tipoMascota == tipo
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFormChange(form.copy(tipoMascota = tipo)) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = when (tipo) {
                            TipoMascota.PERRO -> "Perro"
                            TipoMascota.GATO -> "Gato"
                            TipoMascota.OTRO -> "Otro"
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        OutlinedTextField(
            value = form.senas,
            onValueChange = { onFormChange(form.copy(senas = it)) },
            label = { Text("Señas visibles") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.descripcion,
            onValueChange = { onFormChange(form.copy(descripcion = it)) },
            label = { Text("Descripción breve") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.ubicacion,
            onValueChange = { onFormChange(form.copy(ubicacion = it)) },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "La foto y el GPS real llegan en la próxima entrega; por ahora describe la ubicación arriba.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PasoConfirmacion(nombre: String, modifier: Modifier = Modifier) {
    val numeroCaso by remember { mutableIntStateOf((100..999).random()) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("¡Reporte registrado!", style = MaterialTheme.typography.headlineSmall)
        Text("El caso de $nombre ya está activo y visible para la comunidad.")
        Text("Número de caso simulado: #RH-2026-$numeroCaso", style = MaterialTheme.typography.bodySmall)
    }
}
