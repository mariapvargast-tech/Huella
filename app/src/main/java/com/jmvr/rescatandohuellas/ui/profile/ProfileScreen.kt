package com.jmvr.rescatandohuellas.ui.profile

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jmvr.rescatandohuellas.data.SampleData

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    PanelPerfil(
        perfil = SampleData.perfil,
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    )
}
