# Rescatando Huellas — MVP Fase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship a navigable, visually-consistent Android MVP of Rescatando Huellas (bottom nav + drawer, Rescates/Home screen, a 2-step Reportar flow, and simple placeholder screens for Mapa/Comunidad/Adopción/Perfil), built as one reviewable PR per screen/feature, with a `Toast` on anything out of scope.

**Architecture:** Plain Compose state hoisting (no `ViewModel`) — a small `HuellaAppState` class created with `remember` holds the current bottom-tab destination and the emergency-mode toggle; a `ReportFlowManager` holds the 2-step Reportar wizard state. Simulated data lives in `data/SampleData.kt`. Pure wizard-transition logic is unit-tested; everything else is verified by compiling and running the app.

**Tech Stack:** Kotlin, Jetpack Compose, Material3 (`androidx.compose.material3` + `androidx.compose.material:material-icons-extended`), JUnit4.

---

## Before you start

The repo currently has an **uncommitted, broken** `MainActivity.kt` (drawer + a 5-step Reportar wizard, all in one file) and an uncommitted `ReportFlowTest.kt`. It does not compile: `Icons.Default.*` is used without the `material-icons-extended` dependency. This plan replaces that draft entirely, starting with the missing dependency (Task 1), then rebuilding the app screen-by-screen across separate PRs (Tasks 2–9).

Verify your Android SDK is wired up before Task 1:

```bash
cat local.properties
# Expect: sdk.dir=<path to your Android SDK>
```

If missing, create it: `echo "sdk.dir=$ANDROID_HOME" > local.properties` (this file is gitignored).

**Between each task/PR:** stop and let the reviewer look at `git show` (or their editor) before starting the next task. Do not implement multiple tasks back-to-back without a pause — that is the whole point of the "one PR per screen" workflow the user asked for.

---

### Task 1: Fix the build — add `material-icons-extended`

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Add the library alias**

In `gradle/libs.versions.toml`, in the `[libraries]` section, add this line right after `androidx-compose-material3`:

```toml
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
```

- [ ] **Step 2: Add the dependency**

In `app/build.gradle.kts`, in the `dependencies { }` block, add this line right after `implementation(libs.androidx.compose.material3)`:

```kotlin
    implementation(libs.androidx.compose.material.icons.extended)
```

- [ ] **Step 3: Compile to verify the fix**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL` (the currently-uncommitted `MainActivity.kt` will compile now that `Icons.Default.*` resolves).

- [ ] **Step 4: Run the existing unit test**

Run: `./gradlew :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts
git commit -m "fix: add missing material-icons-extended dependency

Icons.Default.Shield/Warning/Notifications/etc. were used without the
library that provides them, so the app didn't compile."
```

(Leave the still-uncommitted `MainActivity.kt` and `ReportFlowTest.kt` as-is — Task 3 and Task 5 replace them.)

---

### Task 2: Foundation — brand theme, data models, navigation enum, app state

**Files:**
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Color.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/data/models.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/data/SampleData.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/navigation/HuellaDestination.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt`

- [ ] **Step 1: Replace the color palette**

Replace the full contents of `app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Color.kt` with:

```kotlin
package com.jmvr.rescatandohuellas.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de marca — Rescatando Huellas
val HuellaOrange = Color(0xFFF2762E)
val HuellaOrangeDark = Color(0xFFC25A17)
val HuellaCoral = Color(0xFFD93A4E)
val HuellaGreen = Color(0xFF12A08A)
val HuellaPurple = Color(0xFF6E56CF)
val HuellaSlateBlue = Color(0xFF45607F)

val HuellaOrangeTint = Color(0xFFFDEBDD)
val HuellaCoralTint = Color(0xFFFCE4E7)
val HuellaGreenTint = Color(0xFFDFF3EF)
val HuellaPurpleTint = Color(0xFFEAE6FB)

val HuellaBackground = Color(0xFFF3F5F7)
val HuellaSurface = Color(0xFFFFFFFF)
val HuellaOnSurfaceMuted = Color(0xFF5A6070)
val HuellaOnBackground = Color(0xFF171A20)
```

- [ ] **Step 2: Wire the palette into the color scheme and disable dynamic color by default**

Replace the full contents of `app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Theme.kt` with:

```kotlin
package com.jmvr.rescatandohuellas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = HuellaOrange,
    onPrimary = Color.White,
    secondary = HuellaGreen,
    onSecondary = Color.White,
    tertiary = HuellaPurple,
    onTertiary = Color.White,
    background = HuellaBackground,
    onBackground = HuellaOnBackground,
    surface = HuellaSurface,
    onSurface = HuellaOnBackground,
    error = HuellaCoral,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = HuellaOrange,
    secondary = HuellaGreen,
    tertiary = HuellaPurple,
    error = HuellaCoral
)

@Composable
fun RescatandoHuellasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // El color de marca es parte de la identidad del producto: no lo
    // pisamos con el color dinámico del wallpaper del usuario (Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

- [ ] **Step 3: Create the domain models**

Create `app/src/main/java/com/jmvr/rescatandohuellas/data/models.kt`:

```kotlin
package com.jmvr.rescatandohuellas.data

enum class TipoMascota { PERRO, GATO, OTRO }

enum class EstadoMascota { EN_BUSQUEDA, AVISTADA, EN_REFUGIO, REENCONTRADA }

enum class TipoReporte { PERDIDA, ENCONTRADA, RIESGO }

data class Mascota(
    val nombre: String,
    val tipo: TipoMascota,
    val senas: String,
    val estado: EstadoMascota,
    val ubicacion: String,
    val tiempo: String,
    val distanciaKm: Double? = null
)

data class Avistamiento(
    val titulo: String,
    val subtitulo: String,
    val tiempo: String
)

data class PuntoAyuda(
    val nombre: String,
    val tipo: String,
    val distanciaKm: Double,
    val detalle: String
)

data class Estadisticas(
    val reportadas: Int,
    val reencontradas: Int,
    val voluntarios: Int
)

data class EstadoZona(
    val emergenciaActiva: Boolean,
    val nombreEmergencia: String,
    val zona: String,
    val tiempoActivacion: String
)
```

- [ ] **Step 4: Create the simulated data**

Create `app/src/main/java/com/jmvr/rescatandohuellas/data/SampleData.kt`:

```kotlin
package com.jmvr.rescatandohuellas.data

object SampleData {
    val estadoZona = EstadoZona(
        emergenciaActiva = true,
        nombreEmergencia = "Inundaciones — Valle de Aburrá",
        zona = "Laureles",
        tiempoActivacion = "hace 5 min"
    )

    val estadisticas = Estadisticas(reportadas = 127, reencontradas = 43, voluntarios = 18)

    val casosCercanos = listOf(
        Mascota(
            nombre = "Gato herido",
            tipo = TipoMascota.GATO,
            senas = "Bajo un carro, Av. 33",
            estado = EstadoMascota.AVISTADA,
            ubicacion = "Av. 33 con 74",
            tiempo = "hace 8 min",
            distanciaKm = 0.7
        ),
        Mascota(
            nombre = "Rocky",
            tipo = TipoMascota.PERRO,
            senas = "Golden, collar azul",
            estado = EstadoMascota.EN_BUSQUEDA,
            ubicacion = "Laureles",
            tiempo = "hace 25 min",
            distanciaKm = 1.2
        ),
        Mascota(
            nombre = "Nube",
            tipo = TipoMascota.GATO,
            senas = "Blanca, sin collar",
            estado = EstadoMascota.REENCONTRADA,
            ubicacion = "Belén",
            tiempo = "hace 1 h",
            distanciaKm = null
        )
    )

    val avistamientosRecientes = listOf(
        Avistamiento("Avistamiento: golden con collar azul", "Coincide con \"Rocky\"", "hace 25 min"),
        Avistamiento("Nube volvió con su familia", "Reportada ayer en Belén", "hace 1 h")
    )

    val puntosAyuda = listOf(
        PuntoAyuda("Clínica San Joaquín", "Veterinaria", 1.2, "Atiende heridos ahora"),
        PuntoAyuda("Hogar de paso Laureles", "Refugio", 2.4, "12 cupos disponibles"),
        PuntoAyuda("Andrés", "Voluntario", 0.8, "Transporte en moto")
    )
}
```

- [ ] **Step 5: Create the navigation destinations**

Create `app/src/main/java/com/jmvr/rescatandohuellas/navigation/HuellaDestination.kt`:

```kotlin
package com.jmvr.rescatandohuellas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector

enum class HuellaDestination(val etiqueta: String, val icono: ImageVector) {
    RESCATES("Rescates", Icons.Default.Home),
    MAPA("Mapa", Icons.Default.LocationOn),
    ADOPCION("Adopción", Icons.Default.Pets),
    COMUNIDAD("Comunidad", Icons.Default.Group),
    PERFIL("Perfil", Icons.Default.Person)
}
```

- [ ] **Step 6: Create the app-level state holder**

Create `app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt`:

```kotlin
package com.jmvr.rescatandohuellas.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jmvr.rescatandohuellas.navigation.HuellaDestination

class HuellaAppState {
    var destino by mutableStateOf(HuellaDestination.RESCATES)
        private set
    var emergenciaActiva by mutableStateOf(true)

    fun irA(destino: HuellaDestination) {
        this.destino = destino
    }
}

@Composable
fun rememberHuellaAppState(): HuellaAppState = remember { HuellaAppState() }
```

- [ ] **Step 7: Compile to verify**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`. (`MainActivity.kt` still has the old, unrelated code — it will still compile since none of these new files are referenced by it yet.)

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Color.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/ui/theme/Theme.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/data/models.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/data/SampleData.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/navigation/HuellaDestination.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt
git commit -m "feat: add brand theme, domain models, sample data and app state

Foundation for the MVP: orange brand palette (dynamic color off),
Mascota/PuntoAyuda/Estadisticas models with simulated data, the 5
bottom-nav destinations, and a plain state holder for the current tab
and the emergency-mode toggle."
```

---

### Task 3: App shell — drawer + bottom bar + placeholder screens

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/components/Toasts.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/components/PlaceholderScreen.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/MainActivity.kt` (replace entirely)
- Delete: `app/src/test/java/com/jmvr/rescatandohuellas/ReportFlowTest.kt`

- [ ] **Step 1: Create the shared "not built yet" Toast helper**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/components/Toasts.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.components

import android.content.Context
import android.widget.Toast

fun Context.mostrarProximaEntrega() {
    Toast.makeText(this, "Disponible en la próxima entrega", Toast.LENGTH_SHORT).show()
}
```

- [ ] **Step 2: Create a reusable placeholder screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/components/PlaceholderScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderScreen(titulo: String, descripcion: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Text(
            descripcion,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = { context.mostrarProximaEntrega() },
            modifier = Modifier.padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Ver más")
        }
    }
}
```

- [ ] **Step 3: Create the app shell**

Create `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`:

```kotlin
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
import androidx.compose.material.icons.filled.Logout
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
import com.jmvr.rescatandohuellas.ui.components.PlaceholderScreen
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
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
                            context.mostrarProximaEntrega()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) }
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
                        onClick = { context.mostrarProximaEntrega() },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = { context.mostrarProximaEntrega() },
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) }
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
                            icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (appState.destino) {
                HuellaDestination.RESCATES -> PlaceholderScreen(
                    titulo = "Rescates",
                    descripcion = "La pantalla de inicio llega en el próximo PR.",
                    modifier = Modifier.padding(innerPadding)
                )
                HuellaDestination.MAPA -> PlaceholderScreen(
                    titulo = "Mapa",
                    descripcion = "Disponible en la próxima entrega.",
                    modifier = Modifier.padding(innerPadding)
                )
                HuellaDestination.ADOPCION -> PlaceholderScreen(
                    titulo = "Adopción",
                    descripcion = "Disponible en la próxima entrega.",
                    modifier = Modifier.padding(innerPadding)
                )
                HuellaDestination.COMUNIDAD -> PlaceholderScreen(
                    titulo = "Comunidad",
                    descripcion = "Disponible en la próxima entrega.",
                    modifier = Modifier.padding(innerPadding)
                )
                HuellaDestination.PERFIL -> PlaceholderScreen(
                    titulo = "Perfil",
                    descripcion = "Disponible en la próxima entrega.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
```

- [ ] **Step 4: Replace `MainActivity.kt` with a thin entry point**

Replace the full contents of `app/src/main/java/com/jmvr/rescatandohuellas/MainActivity.kt` with:

```kotlin
package com.jmvr.rescatandohuellas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jmvr.rescatandohuellas.ui.theme.RescatandoHuellasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RescatandoHuellasTheme {
                HuellaApp()
            }
        }
    }
}
```

- [ ] **Step 5: Delete the now-outdated test**

The existing `ReportFlowTest.kt` tests a `getNextStep` function that no longer exists after this replacement.

```bash
rm app/src/test/java/com/jmvr/rescatandohuellas/ReportFlowTest.kt
```

(Task 5 adds a new test file for the new Reportar logic.)

- [ ] **Step 6: Compile and test to verify**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git add -A app/src/main/java/com/jmvr/rescatandohuellas app/src/test/java/com/jmvr/rescatandohuellas
git commit -m "feat: rebuild app shell with drawer + bottom nav + placeholders

Replaces the ad-hoc single-file draft with HuellaApp.kt: a
ModalNavigationDrawer (primary items + a 'simular emergencia' switch
for demos + secondary items) wrapping a Scaffold with a 5-item bottom
NavigationBar. Every destination shows a placeholder for now; each
gets replaced by its real screen in a following PR. Anything not yet
built shows a Toast instead of doing nothing."
```

---

### Task 4: Rescates (Home) screen

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`

- [ ] **Step 1: Create the Rescates screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoral
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreen
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreenTint
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrange

@Composable
fun RescatesScreen(
    emergenciaActiva: Boolean,
    onIrAMapa: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (emergenciaActiva) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HuellaCoral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "EMERGENCIA ACTIVA · ${SampleData.estadoZona.tiempoActivacion.uppercase()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            SampleData.estadoZona.nombreEmergencia,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            "Tu zona (${SampleData.estadoZona.zona}) está dentro del área afectada",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HuellaGreenTint),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Todo tranquilo en tu zona", fontWeight = FontWeight.Bold, color = HuellaGreen)
                        Text(
                            "Sin alertas activas · ${SampleData.casosCercanos.size} reportes abiertos cerca",
                            color = HuellaOnSurfaceMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Text("Acciones rápidas", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { context.mostrarProximaEntrega() },
                    colors = ButtonDefaults.buttonColors(containerColor = HuellaOrange),
                    modifier = Modifier.weight(1f)
                ) { Text("Reportar") }
                Button(
                    onClick = onIrAMapa,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) { Text("Ver mapa") }
                Button(
                    onClick = { context.mostrarProximaEntrega() },
                    colors = ButtonDefaults.buttonColors(containerColor = HuellaCoral),
                    modifier = Modifier.weight(1f)
                ) { Text("SOS") }
            }
        }

        item {
            Text("Casos cercanos", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        items(SampleData.casosCercanos) { mascota ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(mascota.nombre, fontWeight = FontWeight.Bold)
                    Text(mascota.senas, color = HuellaOnSurfaceMuted, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${mascota.ubicacion} · ${mascota.tiempo}",
                        color = HuellaOnSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text("Avistamientos recientes", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        }
        items(SampleData.avistamientosRecientes) { avistamiento ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(avistamiento.titulo, fontWeight = FontWeight.Medium)
                Text(
                    "${avistamiento.subtitulo} · ${avistamiento.tiempo}",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp)
            ) {
                EstadisticaColumna("reportadas", SampleData.estadisticas.reportadas, Modifier.weight(1f))
                EstadisticaColumna("reencontradas", SampleData.estadisticas.reencontradas, Modifier.weight(1f))
                EstadisticaColumna("voluntarios", SampleData.estadisticas.voluntarios, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EstadisticaColumna(etiqueta: String, valor: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(etiqueta, color = HuellaOnSurfaceMuted, style = MaterialTheme.typography.labelSmall)
    }
}
```

- [ ] **Step 2: Wire it into the app shell**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`, replace:

```kotlin
                HuellaDestination.RESCATES -> PlaceholderScreen(
                    titulo = "Rescates",
                    descripcion = "La pantalla de inicio llega en el próximo PR.",
                    modifier = Modifier.padding(innerPadding)
                )
```

with:

```kotlin
                HuellaDestination.RESCATES -> RescatesScreen(
                    emergenciaActiva = appState.emergenciaActiva,
                    onIrAMapa = { appState.irA(HuellaDestination.MAPA) },
                    modifier = Modifier.padding(innerPadding)
                )
```

Add this import next to the other `com.jmvr.rescatandohuellas.ui.components...` imports:

```kotlin
import com.jmvr.rescatandohuellas.ui.home.RescatesScreen
```

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt
git commit -m "feat: implement Rescates (home) screen

Emergency/calm banner driven by the drawer's 'simular emergencia'
switch, quick-action buttons (Reportar/SOS still show the
not-implemented Toast until their own PR), casos cercanos and
avistamientos recientes lists, and a stats row — all from SampleData."
```

---

### Task 5: Reportar flow (2-step wizard + confirmation)

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/state/ReportFlowState.kt`
- Create: `app/src/test/java/com/jmvr/rescatandohuellas/ReportFlowStateTest.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportFlowScreen.kt`
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportSteps.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt`

- [ ] **Step 1: Write the failing test for the wizard's pure logic**

Create `app/src/test/java/com/jmvr/rescatandohuellas/ReportFlowStateTest.kt`:

```kotlin
package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.state.PASO_CONFIRMACION
import com.jmvr.rescatandohuellas.state.PASO_DATOS
import com.jmvr.rescatandohuellas.state.PASO_TIPO
import com.jmvr.rescatandohuellas.state.ReporteForm
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeDatos
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeTipo
import com.jmvr.rescatandohuellas.state.siguientePaso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportFlowStateTest {
    @Test
    fun `no se puede avanzar del paso tipo sin elegir tipo`() {
        assertFalse(puedeAvanzarDesdeTipo(ReporteForm()))
        assertTrue(puedeAvanzarDesdeTipo(ReporteForm(tipo = TipoReporte.PERDIDA)))
    }

    @Test
    fun `no se puede avanzar del paso datos sin nombre o ubicacion`() {
        val base = ReporteForm(tipo = TipoReporte.PERDIDA)
        assertFalse(puedeAvanzarDesdeDatos(base))
        assertFalse(puedeAvanzarDesdeDatos(base.copy(nombre = "Luna")))
        assertTrue(puedeAvanzarDesdeDatos(base.copy(nombre = "Luna", ubicacion = "Laureles")))
    }

    @Test
    fun `siguientePaso solo avanza cuando el paso actual es valido`() {
        assertEquals(PASO_TIPO, siguientePaso(PASO_TIPO, ReporteForm()))

        val conTipo = ReporteForm(tipo = TipoReporte.ENCONTRADA)
        assertEquals(PASO_DATOS, siguientePaso(PASO_TIPO, conTipo))

        val datosIncompletos = conTipo.copy(nombre = "Rocky")
        assertEquals(PASO_DATOS, siguientePaso(PASO_DATOS, datosIncompletos))

        val datosCompletos = conTipo.copy(nombre = "Rocky", ubicacion = "Belén")
        assertEquals(PASO_CONFIRMACION, siguientePaso(PASO_DATOS, datosCompletos))

        assertEquals(PASO_CONFIRMACION, siguientePaso(PASO_CONFIRMACION, datosCompletos))
    }
}
```

- [ ] **Step 2: Run it to verify it fails to compile (the logic doesn't exist yet)**

Run: `./gradlew :app:testDebugUnitTest --console=plain`
Expected: FAIL — `Unresolved reference` for `PASO_TIPO`, `ReporteForm`, etc.

- [ ] **Step 3: Implement the wizard state and pure logic**

Create `app/src/main/java/com/jmvr/rescatandohuellas/state/ReportFlowState.kt`:

```kotlin
package com.jmvr.rescatandohuellas.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jmvr.rescatandohuellas.data.TipoMascota
import com.jmvr.rescatandohuellas.data.TipoReporte

const val PASO_TIPO = 1
const val PASO_DATOS = 2
const val PASO_CONFIRMACION = 3

data class ReporteForm(
    val tipo: TipoReporte? = null,
    val nombre: String = "",
    val tipoMascota: TipoMascota = TipoMascota.PERRO,
    val senas: String = "",
    val descripcion: String = "",
    val ubicacion: String = ""
)

fun puedeAvanzarDesdeTipo(form: ReporteForm): Boolean = form.tipo != null

fun puedeAvanzarDesdeDatos(form: ReporteForm): Boolean =
    form.nombre.isNotBlank() && form.ubicacion.isNotBlank()

fun siguientePaso(pasoActual: Int, form: ReporteForm): Int = when (pasoActual) {
    PASO_TIPO -> if (puedeAvanzarDesdeTipo(form)) PASO_DATOS else PASO_TIPO
    PASO_DATOS -> if (puedeAvanzarDesdeDatos(form)) PASO_CONFIRMACION else PASO_DATOS
    else -> PASO_CONFIRMACION
}

class ReportFlowManager {
    var paso by mutableStateOf(PASO_TIPO)
        private set
    var form by mutableStateOf(ReporteForm())
        private set

    fun actualizarForm(nuevo: ReporteForm) {
        form = nuevo
    }

    fun avanzar() {
        paso = siguientePaso(paso, form)
    }

    /** @return false si ya estaba en el primer paso (nada que retroceder). */
    fun retroceder(): Boolean {
        if (paso <= PASO_TIPO) return false
        paso -= 1
        return true
    }

    fun reiniciar() {
        paso = PASO_TIPO
        form = ReporteForm()
    }
}

@Composable
fun rememberReportFlowManager(): ReportFlowManager = remember { ReportFlowManager() }
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`, all `ReportFlowStateTest` cases green.

- [ ] **Step 5: Build the step composables**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportSteps.kt`:

```kotlin
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
    var numeroCaso by remember { mutableIntStateOf((100..999).random()) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("¡Reporte registrado!", style = MaterialTheme.typography.headlineSmall)
        Text("El caso de $nombre ya está activo y visible para la comunidad.")
        Text("Número de caso simulado: #RH-2026-$numeroCaso", style = MaterialTheme.typography.bodySmall)
    }
}
```

- [ ] **Step 6: Build the wizard container**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportFlowScreen.kt`:

```kotlin
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
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.state.PASO_CONFIRMACION
import com.jmvr.rescatandohuellas.state.PASO_DATOS
import com.jmvr.rescatandohuellas.state.PASO_TIPO
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
            Button(
                onClick = { manager.avanzar() },
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
```

- [ ] **Step 7: Add report-flow navigation to the app state**

In `app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt`, replace the class body:

```kotlin
class HuellaAppState {
    var destino by mutableStateOf(HuellaDestination.RESCATES)
        private set
    var emergenciaActiva by mutableStateOf(true)

    fun irA(destino: HuellaDestination) {
        this.destino = destino
    }
}
```

with:

```kotlin
class HuellaAppState {
    var destino by mutableStateOf(HuellaDestination.RESCATES)
        private set
    var emergenciaActiva by mutableStateOf(true)
    var reportando by mutableStateOf(false)
        private set

    fun irA(destino: HuellaDestination) {
        this.destino = destino
        this.reportando = false
    }

    fun abrirReportar() {
        reportando = true
    }

    fun cerrarReportar() {
        reportando = false
    }
}
```

- [ ] **Step 8: Wire the flow into the app shell**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`:

1. Add these imports next to the others:

```kotlin
import com.jmvr.rescatandohuellas.ui.report.ReportFlowScreen
```

2. Replace the drawer's "Reportar" item onClick:

```kotlin
                    NavigationDrawerItem(
                        label = { Text("Reportar") },
                        selected = false,
                        onClick = {
                            context.mostrarProximaEntrega()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) }
                    )
```

with:

```kotlin
                    NavigationDrawerItem(
                        label = { Text("Reportar") },
                        selected = false,
                        onClick = {
                            appState.abrirReportar()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Warning, contentDescription = null) }
                    )
```

3. Replace the `when (appState.destino) { ... }` content block inside the `Scaffold` with:

```kotlin
            if (appState.reportando) {
                ReportFlowScreen(
                    onFinalizar = { appState.cerrarReportar() },
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
                    HuellaDestination.MAPA -> PlaceholderScreen(
                        titulo = "Mapa",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
                    HuellaDestination.ADOPCION -> PlaceholderScreen(
                        titulo = "Adopción",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
                    HuellaDestination.COMUNIDAD -> PlaceholderScreen(
                        titulo = "Comunidad",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
                    HuellaDestination.PERFIL -> PlaceholderScreen(
                        titulo = "Perfil",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
```

- [ ] **Step 9: Let Rescates open the real flow instead of showing a Toast**

In `app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt`:

1. Change the function signature from:

```kotlin
fun RescatesScreen(
    emergenciaActiva: Boolean,
    onIrAMapa: () -> Unit,
    modifier: Modifier = Modifier
) {
```

to:

```kotlin
fun RescatesScreen(
    emergenciaActiva: Boolean,
    onIrAMapa: () -> Unit,
    onIrAReportar: () -> Unit,
    modifier: Modifier = Modifier
) {
```

2. Change the "Reportar" quick-action button's `onClick` from:

```kotlin
                Button(
                    onClick = { context.mostrarProximaEntrega() },
                    colors = ButtonDefaults.buttonColors(containerColor = HuellaOrange),
                    modifier = Modifier.weight(1f)
                ) { Text("Reportar") }
```

to:

```kotlin
                Button(
                    onClick = onIrAReportar,
                    colors = ButtonDefaults.buttonColors(containerColor = HuellaOrange),
                    modifier = Modifier.weight(1f)
                ) { Text("Reportar") }
```

- [ ] **Step 10: Compile and test to verify**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 11: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/state/ReportFlowState.kt \
        app/src/test/java/com/jmvr/rescatandohuellas/ReportFlowStateTest.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportFlowScreen.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/ui/report/ReportSteps.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/state/HuellaAppState.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/ui/home/RescatesScreen.kt
git commit -m "feat: implement the 2-step Reportar flow

Paso 1 (tipo: perdida/encontrada/riesgo) -> paso 2 (formulario
combinado: nombre, señas, descripción, ubicación) -> confirmación con
número de caso simulado. Matches the official master doc's
high-fidelity flow and RNF01 (fast reporting under pressure). Wizard
transition/validation logic is unit-tested; Reportar is now reachable
from the Rescates screen and the drawer instead of showing a Toast."
```

---

### Task 6: Mapa screen

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/map/MapScreen.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`

- [ ] **Step 1: Create the screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/map/MapScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Red de ayuda cercana", style = MaterialTheme.typography.titleLarge)
                Text(
                    "El mapa interactivo llega en la próxima entrega. Por ahora, esta es la lista de puntos de ayuda.",
                    color = HuellaOnSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        items(SampleData.puntosAyuda) { punto ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
```

- [ ] **Step 2: Wire it in**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`, replace:

```kotlin
                    HuellaDestination.MAPA -> PlaceholderScreen(
                        titulo = "Mapa",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
```

with:

```kotlin
                    HuellaDestination.MAPA -> MapScreen(modifier = Modifier.padding(innerPadding))
```

Add the import: `import com.jmvr.rescatandohuellas.ui.map.MapScreen`

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/ui/map/MapScreen.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt
git commit -m "feat: implement Mapa as a simple list of puntos de ayuda

No real map SDK in this delivery (out of scope per the design spec) —
lists refugios/veterinarias/voluntarios from SampleData instead."
```

---

### Task 7: Comunidad screen

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/community/CommunityScreen.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`

- [ ] **Step 1: Create the screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/community/CommunityScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.data.SampleData
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun CommunityScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Comunidad", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { context.mostrarProximaEntrega() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${SampleData.estadisticas.voluntarios} voluntarios activos")
                Text(
                    "${SampleData.puntosAyuda.count { it.tipo == "Refugio" }} refugios cerca",
                    color = HuellaOnSurfaceMuted
                )
                Text(
                    "Feed de historias y guías disponible en la próxima entrega",
                    color = HuellaOnSurfaceMuted
                )
            }
        }
    }
}
```

- [ ] **Step 2: Wire it in**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`, replace:

```kotlin
                    HuellaDestination.COMUNIDAD -> PlaceholderScreen(
                        titulo = "Comunidad",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
```

with:

```kotlin
                    HuellaDestination.COMUNIDAD -> CommunityScreen(modifier = Modifier.padding(innerPadding))
```

Add the import: `import com.jmvr.rescatandohuellas.ui.community.CommunityScreen`

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/ui/community/CommunityScreen.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt
git commit -m "feat: implement Comunidad stats card"
```

---

### Task 8: Adopción screen (placeholder, per the reconciled scope)

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/adoption/AdopcionScreen.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`

- [ ] **Step 1: Create the screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/adoption/AdopcionScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

private data class AnimalEnAdopcion(val nombre: String, val tipo: String, val edad: String)

private val animalesDeEjemplo = listOf(
    AnimalEnAdopcion("Milo", "Perro mestizo", "2 años"),
    AnimalEnAdopcion("Cleo", "Gata siamés", "1 año")
)

@Composable
fun AdopcionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Adopción", style = MaterialTheme.typography.titleLarge)
        Text(
            "Publicar, filtrar y adoptar llega en la próxima entrega.",
            color = HuellaOnSurfaceMuted,
            style = MaterialTheme.typography.bodySmall
        )
        animalesDeEjemplo.forEach { animal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { context.mostrarProximaEntrega() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(animal.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("${animal.tipo} · ${animal.edad}", color = HuellaOnSurfaceMuted)
                }
            }
        }
    }
}
```

- [ ] **Step 2: Wire it in**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`, replace:

```kotlin
                    HuellaDestination.ADOPCION -> PlaceholderScreen(
                        titulo = "Adopción",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
```

with:

```kotlin
                    HuellaDestination.ADOPCION -> AdopcionScreen(modifier = Modifier.padding(innerPadding))
```

Add the import: `import com.jmvr.rescatandohuellas.ui.adoption.AdopcionScreen`

- [ ] **Step 3: Compile to verify**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/jmvr/rescatandohuellas/ui/adoption/AdopcionScreen.kt \
        app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt
git commit -m "feat: add Adopción placeholder screen

Kept as a real tab (matches the official nav structure) but with only
2 example cards for now; full adoption flow (RF32-35) is fase 2."
```

---

### Task 9: Perfil screen + remove the generic placeholder

**Files:**
- Create: `app/src/main/java/com/jmvr/rescatandohuellas/ui/profile/ProfileScreen.kt`
- Modify: `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`
- Delete: `app/src/main/java/com/jmvr/rescatandohuellas/ui/components/PlaceholderScreen.kt`

- [ ] **Step 1: Create the screen**

Create `app/src/main/java/com/jmvr/rescatandohuellas/ui/profile/ProfileScreen.kt`:

```kotlin
package com.jmvr.rescatandohuellas.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jmvr.rescatandohuellas.ui.components.mostrarProximaEntrega
import com.jmvr.rescatandohuellas.ui.theme.HuellaOnSurfaceMuted

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Perfil", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("María Paula Vargas", style = MaterialTheme.typography.titleMedium)
                Text("Laureles · Voluntaria activa", color = HuellaOnSurfaceMuted)
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { context.mostrarProximaEntrega() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Editar perfil", style = MaterialTheme.typography.titleMedium)
                Text("Mis mascotas y mi impacto llegan en la próxima entrega.", color = HuellaOnSurfaceMuted)
            }
        }
    }
}
```

- [ ] **Step 2: Wire it in and drop the now-unused placeholder**

In `app/src/main/java/com/jmvr/rescatandohuellas/HuellaApp.kt`:

1. Replace:

```kotlin
                    HuellaDestination.PERFIL -> PlaceholderScreen(
                        titulo = "Perfil",
                        descripcion = "Disponible en la próxima entrega.",
                        modifier = Modifier.padding(innerPadding)
                    )
```

with:

```kotlin
                    HuellaDestination.PERFIL -> ProfileScreen(modifier = Modifier.padding(innerPadding))
```

2. Remove the now-unused import `import com.jmvr.rescatandohuellas.ui.components.PlaceholderScreen` and add `import com.jmvr.rescatandohuellas.ui.profile.ProfileScreen`.

- [ ] **Step 3: Delete the generic placeholder (no destination uses it anymore)**

```bash
rm app/src/main/java/com/jmvr/rescatandohuellas/ui/components/PlaceholderScreen.kt
```

- [ ] **Step 4: Compile and test the whole app to verify**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add -A app/src/main/java/com/jmvr/rescatandohuellas
git commit -m "feat: implement Perfil screen and remove unused PlaceholderScreen

All 5 bottom-nav destinations now have a real (if simple, per the fase
1 scope) screen. This closes out the fase 1 MVP plan."
```

---

## Manual verification (after Task 9)

The `run` skill can install and launch the app on a device/emulator. At minimum, verify by hand:

1. App opens on Rescates with the emergency banner showing (SampleData defaults to `emergenciaActiva = true`).
2. Drawer → toggle "Simular emergencia" off → banner switches to the calm state on Rescates.
3. Tap "Reportar" on Rescates → pick "Mascota perdida" → fill nombre + ubicación → "Publicar reporte" → confirmation screen with a case number → "Volver a Rescates" returns to Rescates.
4. Bottom bar cycles through Rescates/Mapa/Adopción/Comunidad/Perfil without crashing; Mapa shows the 3 sample puntos de ayuda.
5. Any button not yet wired (SOS, Red de ayuda, Configuración, Cerrar sesión, the Adopción/Comunidad/Perfil cards) shows the "Disponible en la próxima entrega" Toast.

## Explicit follow-ups (fase 2, not in this plan)

Same list as the design spec's "Pendiente explícito para fase 2" section — Adopción funcional, Red de ayuda dedicada, Comunidad completa, Perfil completo, mapa real, auth/backend real, notificaciones, fuentes de marca reales, cámara/GPS reales, y decidir si se migra a la paleta verde/teal de los mockups de alta resolución.
