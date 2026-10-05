package com.jmvr.rescatandohuellas.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.PerfilPersona
import com.jmvr.rescatandohuellas.state.EdicionPerfil
import com.jmvr.rescatandohuellas.state.aplicar
import com.jmvr.rescatandohuellas.state.esValida
import com.jmvr.rescatandohuellas.ui.components.barraDesplazamiento
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreenTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun PanelPerfil(perfil: PerfilPersona, onGuardar: (PerfilPersona) -> Unit, modifier: Modifier = Modifier) {
    var editando by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = editando) { editando = false }
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
        if (editando) {
            FormularioPerfil(
                perfil = perfil,
                onGuardar = {
                    onGuardar(it)
                    editando = false
                },
                onCancelar = { editando = false }
            )
        } else {
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
            TextButton(onClick = { editando = true }) {
                Text("Editar perfil")
            }
        }
        Text(
            "Ilustración de perfil: OpenMoji – CC BY-SA 4.0",
            color = HuellaOnSurfaceMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FormularioPerfil(
    perfil: PerfilPersona,
    onGuardar: (PerfilPersona) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(perfil.nombre) }
    var rol by rememberSaveable { mutableStateOf(perfil.rol) }
    var acercaDe by rememberSaveable { mutableStateOf(perfil.acercaDe) }
    var estudios by rememberSaveable { mutableStateOf(perfil.estudios) }
    var experiencia by rememberSaveable { mutableStateOf(perfil.experiencia) }
    val edicion = EdicionPerfil(nombre, rol, acercaDe, estudios, experiencia)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            isError = !edicion.esValida(),
            supportingText = if (!edicion.esValida()) {
                { Text("El nombre es obligatorio") }
            } else null,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = rol,
            onValueChange = { rol = it },
            label = { Text("Rol y ubicación") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = acercaDe,
            onValueChange = { acercaDe = it },
            label = { Text("Acerca de mí") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estudios,
            onValueChange = { estudios = it },
            label = { Text("Estudios") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = experiencia,
            onValueChange = { experiencia = it },
            label = { Text("Experiencia") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
            Button(
                onClick = { perfil.aplicar(edicion)?.let(onGuardar) },
                enabled = edicion.esValida()
            ) { Text("Guardar") }
        }
    }
}

@Composable
private fun CampoTextoPerfil(titulo: String, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(texto, style = MaterialTheme.typography.bodySmall)
        }
    }
}
