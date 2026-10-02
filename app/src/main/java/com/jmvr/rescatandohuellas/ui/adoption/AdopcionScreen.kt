package com.jmvr.rescatandohuellas.ui.adoption

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.FotoGaleria
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.components.barraDesplazamiento
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun AdopcionScreen(modifier: Modifier = Modifier) {
    var seleccionadaId by rememberSaveable { mutableStateOf<Int?>(null) }
    val lista = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Text("Adopción", style = MaterialTheme.typography.titleLarge)
            Text(
                "Toca una foto para ver su descripción.",
                color = HuellaOnSurfaceMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
        LazyColumn(
            state = lista,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .barraDesplazamiento(lista, MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(start = 16.dp, end = 20.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SampleData.fotosAdopcion, key = { it.id }) { foto ->
                TarjetaFoto(
                    foto = foto,
                    seleccionada = foto.id == seleccionadaId,
                    onClick = { seleccionadaId = if (seleccionadaId == foto.id) null else foto.id }
                )
            }
        }
    }
}

@Composable
private fun TarjetaFoto(foto: FotoGaleria, seleccionada: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (seleccionada) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Image(
            painter = painterResource(foto.imagen),
            contentDescription = foto.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(foto.titulo, style = MaterialTheme.typography.titleMedium)
            AnimatedVisibility(visible = seleccionada) {
                Column {
                    Text(
                        foto.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    TextButton(onClick = { context.mostrarProximaEntrega() }) {
                        Text("Quiero adoptar")
                    }
                }
            }
        }
    }
}
