package com.jmvr.rescatandohuellas.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.PerfilPersona
import com.jmvr.rescatandohuellas.ui.components.barraDesplazamiento
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreenTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun PanelPerfil(perfil: PerfilPersona, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .barraDesplazamiento(scroll, HuellaOnSurfaceMuted)
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(perfil.foto),
            contentDescription = "Foto de ${perfil.nombre}",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(HuellaGreenTint)
                .padding(8.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(perfil.nombre, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(
                perfil.rol,
                color = HuellaOnSurfaceMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        CampoTextoPerfil(titulo = "Acerca de mí", texto = perfil.acercaDe)
        CampoTextoPerfil(titulo = "Estudios", texto = perfil.estudios)
        CampoTextoPerfil(titulo = "Experiencia", texto = perfil.experiencia)
        TextButton(onClick = { context.mostrarProximaEntrega() }) {
            Text("Editar perfil")
        }
        Text(
            "Ilustración de perfil: OpenMoji – CC BY-SA 4.0",
            color = HuellaOnSurfaceMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/** Campo de texto largo con alto fijo y su propia barra de desplazamiento, para ahorrar pantalla. */
@Composable
private fun CampoTextoPerfil(titulo: String, texto: String) {
    val scroll = rememberScrollState()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                texto,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .barraDesplazamiento(scroll, MaterialTheme.colorScheme.primary)
                    .verticalScroll(scroll)
                    .padding(end = 12.dp)
            )
        }
    }
}
