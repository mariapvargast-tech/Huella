package com.jmvr.rescatandohuellas.ui.help

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.CategoriaAyuda
import com.jmvr.rescatandohuellas.data.RecursoAyuda
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.state.filtrarRecursosAyuda
import com.jmvr.rescatandohuellas.ui.components.barraDesplazamiento
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoral
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoralTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreen
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreenTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrange

@Composable
fun RedAyudaScreen(onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    BackHandler(onBack = onCerrar)
    val context = LocalContext.current
    var categoria by rememberSaveable { mutableStateOf(CategoriaAyuda.REFUGIO) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    val recursos = filtrarRecursosAyuda(SampleData.recursosAyuda, categoria, busqueda)
    val lista = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar refugios, veterinarias...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CategoriaAyuda.entries) { opcion ->
                    FilterChip(
                        selected = opcion == categoria,
                        onClick = { categoria = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }
            if (recursos.isEmpty()) {
                Text(
                    "No se encontraron resultados.",
                    color = HuellaOnSurfaceMuted,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn(
                state = lista,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .barraDesplazamiento(lista, HuellaOnSurfaceMuted),
                contentPadding = PaddingValues(start = 16.dp, end = 20.dp, top = 4.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recursos, key = { it.id }) { recurso ->
                    TarjetaRecurso(recurso, onAccion = { context.mostrarProximaEntrega() })
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { context.mostrarProximaEntrega() },
            containerColor = HuellaCoral,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("SOS", fontWeight = FontWeight.Bold)
            Text("  Solicitar ayuda urgente")
        }
    }
}

@Composable
private fun TarjetaRecurso(recurso: RecursoAyuda, onAccion: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    recurso.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                InsigniaDisponibilidad(recurso.disponible)
            }
            Text(recurso.detalle, style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = HuellaCoral,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    "${recurso.distanciaKm} km",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAccion) { Text("Ver") }
                OutlinedButton(onClick = onAccion) { Text("Ruta") }
                Button(onClick = onAccion) { Text("Solicitar ayuda") }
            }
        }
    }
}

/** El estado va en texto y no solo en color (RNF02). */
@Composable
private fun InsigniaDisponibilidad(disponible: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (disponible) HuellaGreenTint else HuellaCoralTint
    ) {
        Text(
            if (disponible) "Disponible" else "Lleno",
            color = if (disponible) HuellaGreen else HuellaOrange,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
