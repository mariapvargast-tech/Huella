package com.jmvr.rescatandohuellas.ui.profile

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jmvr.rescatandohuellas.data.PerfilPersona

@Composable
fun ProfileScreen(perfil: PerfilPersona, onGuardar: (PerfilPersona) -> Unit, modifier: Modifier = Modifier) {
    PanelPerfil(
        perfil = perfil,
        onGuardar = onGuardar,
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    )
}
