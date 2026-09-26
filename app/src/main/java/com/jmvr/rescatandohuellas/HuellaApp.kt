package com.jmvr.rescatandohuellas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.navigation.HuellaDestination
import com.jmvr.rescatandohuellas.state.rememberHuellaAppState
import com.jmvr.rescatandohuellas.ui.adoption.AdopcionScreen
import com.jmvr.rescatandohuellas.ui.community.CommunityScreen
import com.jmvr.rescatandohuellas.ui.about.AboutScreen
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.home.RescatesScreen
import com.jmvr.rescatandohuellas.ui.map.MapScreen
import com.jmvr.rescatandohuellas.ui.profile.ProfileScreen
import com.jmvr.rescatandohuellas.ui.report.ReportFlowScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuellaApp() {
    val appState = rememberHuellaAppState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || appState.destino != HuellaDestination.MAPA,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Text("Rescatando Huellas", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HuellaDestination.entries.forEach { destino ->
                        NavigationDrawerItem(
                            label = { Text(destino.etiqueta) },
                            selected = appState.destino == destino,
                            onClick = {
                                appState.irA(destino)
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(destino.icono, contentDescription = null) }
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text("Reportar") },
                        selected = false,
                        onClick = {
                            appState.abrirReportar()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Acerca de") },
                        selected = false,
                        onClick = {
                            appState.abrirAcercaDe()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Red de ayuda") },
                        selected = false,
                        onClick = {
                            context.mostrarProximaEntrega()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Shield, contentDescription = null) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simular emergencia")
                        Switch(
                            checked = appState.emergenciaActiva,
                            onCheckedChange = { appState.emergenciaActiva = it }
                        )
                    }

                    NavigationDrawerItem(
                        label = { Text("Configuración") },
                        selected = false,
                        onClick = {
                            context.mostrarProximaEntrega()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = {
                            context.mostrarProximaEntrega()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rescatando Huellas") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { context.mostrarProximaEntrega() }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    HuellaDestination.entries.forEach { destino ->
                        NavigationBarItem(
                            selected = appState.destino == destino,
                            onClick = { appState.irA(destino) },
                            icon = { Icon(destino.icono, contentDescription = null) },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            if (appState.reportando) {
                ReportFlowScreen(
                    onFinalizar = { appState.cerrarReportar() },
                    modifier = Modifier.padding(innerPadding)
                )
            } else if (appState.mostrandoAcercaDe) {
                AboutScreen(
                    onCerrar = { appState.cerrarAcercaDe() },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                when (appState.destino) {
                    HuellaDestination.RESCATES -> RescatesScreen(
                        emergenciaActiva = appState.emergenciaActiva,
                        onIrAMapa = { appState.irA(HuellaDestination.MAPA) },
                        onIrAReportar = { appState.abrirReportar() },
                        modifier = Modifier.padding(innerPadding)
                    )
                    HuellaDestination.MAPA -> MapScreen(
                        mapa = appState.mapaReportes,
                        modifier = Modifier.padding(innerPadding)
                    )
                    HuellaDestination.ADOPCION -> AdopcionScreen(modifier = Modifier.padding(innerPadding))
                    HuellaDestination.COMUNIDAD -> CommunityScreen(modifier = Modifier.padding(innerPadding))
                    HuellaDestination.PERFIL -> ProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
