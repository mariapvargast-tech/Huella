package com.jmvr.rescatandohuellas.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.state.PASO_CONFIRMACION
import com.jmvr.rescatandohuellas.state.PASO_DATOS
import com.jmvr.rescatandohuellas.state.PASO_TIPO
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeDatos
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeTipo
import com.jmvr.rescatandohuellas.state.rememberReportFlowManager
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrange

@Composable
fun ReportFlowScreen(onFinalizar: () -> Unit, modifier: Modifier = Modifier) {
    val manager = rememberReportFlowManager()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (manager.paso != PASO_CONFIRMACION) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (!manager.retroceder()) onFinalizar()
                        },
                    contentAlignment = Alignment.Center
                ) { Text("‹") }
                Spacer(modifier = Modifier.size(12.dp))
            }
            Text(
                when (manager.paso) {
                    PASO_TIPO -> "¿Qué ocurrió?"
                    PASO_DATOS -> "Datos del caso"
                    else -> "Reporte publicado"
                },
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (manager.paso != PASO_CONFIRMACION) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 12.dp)) {
                repeat(2) { indice ->
                    val activo = manager.paso >= indice + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (activo) HuellaOrange else MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (manager.paso) {
            PASO_TIPO -> PasoTipo(form = manager.form, onFormChange = manager::actualizarForm)
            PASO_DATOS -> PasoDatos(form = manager.form, onFormChange = manager::actualizarForm)
            else -> PasoConfirmacion(nombre = manager.form.nombre.ifBlank { "tu reporte" })
        }

        Spacer(modifier = Modifier.weight(1f))

        if (manager.paso != PASO_CONFIRMACION) {
            val puedeAvanzar = when (manager.paso) {
                PASO_TIPO -> puedeAvanzarDesdeTipo(manager.form)
                PASO_DATOS -> puedeAvanzarDesdeDatos(manager.form)
                else -> true
            }
            Button(
                onClick = { manager.avanzar() },
                enabled = puedeAvanzar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HuellaOrange)
            ) {
                Text(if (manager.paso == PASO_DATOS) "Publicar reporte" else "Continuar")
            }
        } else {
            Button(
                onClick = {
                    manager.reiniciar()
                    onFinalizar()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Volver a Rescates")
            }
        }
    }
}