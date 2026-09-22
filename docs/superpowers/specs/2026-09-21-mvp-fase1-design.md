# Rescatando Huellas — MVP Fase 1 (diseño)

## Contexto

El repo ya tenía un `MainActivity.kt` sin commitear con drawer, Home, un wizard de
Reportar de 5 pasos y stubs de Comunidad/Perfil, todo en un solo archivo. Se
decidió refactorizar y extender ese trabajo en vez de reescribirlo desde cero.

Durante el brainstorming el usuario compartió el "Documento Maestro" del
proyecto académico (RF/RNF, casos de uso, diagramas UML, flujo de navegación y
wireframes de baja/media/alta resolución). Ese documento es la fuente
autoritativa del proyecto y tiene algunas diferencias con el mockup
`Huella.dc.html` usado en la primera pasada de diseño. Las decisiones abajo ya
reconcilian ambas fuentes.

## Decisiones de alcance y fidelidad

- **Nombre de marca**: "Rescatando Huellas" (coincide con el namespace del
  código `com.jmvr.rescatandohuellas` y el README). Se descarta el nombre
  "Huella" del mockup dc.html.
- **Paleta**: naranja (`#F2762E` primario, coral `#D93A4E`, verde `#12A08A`,
  morado `#6E56CF`, azul pizarra `#45607F`), la misma que ya usaba el código
  sin commitear y el mockup dc.html. Se descarta la paleta verde/teal de los
  wireframes de alta resolución (Figuras 39–54) para esta entrega.
- **Navegación**: se sigue la estructura del documento oficial (Figura 11 y
  mockups de alta resolución) en vez de la propuesta inicial basada en
  dc.html:
  - Bottom bar de 5 destinos: **Rescates** (home/inicio), **Mapa**,
    **Adopción**, **Comunidad**, **Perfil**. No hay pestaña "Reportar" ni FAB
    central — "Reportar" es un botón dentro de la pantalla Rescates.
  - Drawer lateral con: Inicio, Reportar, Mapa, Red de ayuda, Comunidad,
    Adopción, Perfil, y una sección secundaria con **"Simular emergencia"**
    (switch para alternar entre estado normal/emergencia en la demo, ya que no
    hay backend) y "Cerrar sesión" (simulado, sin auth real).
- **Adopción**: aparece como pestaña (para que la navegación calce con el
  documento oficial) pero su contenido es solo un placeholder simple
  ("próximamente" + 1-2 tarjetas de ejemplo), igual de liviano que
  Comunidad/Perfil. La funcionalidad real de adopción (RF32-35) queda para
  fase 2.
- **Red de ayuda**: no es una pantalla propia en fase 1; sus datos (refugios,
  veterinarias, voluntarios) se muestran dentro de Mapa como una lista simple.
  Una pantalla dedicada de Red de ayuda (con detalle de punto, solicitar
  ayuda, seguimiento) queda para fase 2.
- **Flujo de Reportar — reconciliado**: el pedido original mencionaba 4 pasos
  (situación, foto, ubicación, información esencial) + confirmación, pero el
  documento oficial (RNF01 "uso rápido bajo presión" + mockups de alta
  resolución, Figuras 41/42/44/45) usa solo **2 pasos**: elegir tipo, y un
  formulario combinado (foto + datos básicos + ubicación) específico según el
  tipo, seguido de una confirmación con número de seguimiento. Se sigue el
  patrón de 2 pasos del documento oficial porque cumple mejor la RNF01 y es
  más fiel al entregable académico, pero se conservan los mismos campos de
  datos que pedía el mensaje original (foto, ubicación, nombre/tipo/señas,
  descripción), solo que agrupados en una sola pantalla scrolleable en vez de
  pantallas separadas.
  - Paso 1: "¿Qué ocurrió?" — Mascota perdida / Animal encontrado / Animal en
    situación de riesgo (este último con estilo "SOS" urgente, rojo).
  - Paso 2: formulario combinado — foto (simulada, sin cámara real), nombre,
    especie/tipo, señas (chips), descripción, ubicación (simulada con
    "usar mi ubicación" / "elegir en el mapa").
  - Paso 3 (confirmación): número de caso simulado, resumen, "próximos pasos
    automatizados" (cruce de fotos, alertas a voluntarios/refugios — todo
    simulado/estático), botones para ver el caso o volver a Rescates.
- **Fase de esta entrega**: solo Fase 1 (navegación + Rescates/Home +
  Reportar + base visual). Comunidad, Perfil y Adopción quedan como
  placeholders funcionales pero simples.
- **Acciones no implementadas**: cualquier botón, tarjeta o ítem de navegación
  que apunte a algo fuera de alcance de esta entrega (Adopción real, Red de
  ayuda dedicada, cámara/GPS reales, notificaciones, cerrar sesión real,
  configuración, etc.) debe mostrar un `Toast` con un mensaje como
  "Disponible en la próxima entrega" en vez de no hacer nada o estar
  deshabilitado sin explicación.
- **Entrega incremental**: la implementación se divide en un PR por
  pantalla/feature (ver plan de implementación) en vez de un solo PR con todo
  el alcance, para permitir revisión de código en cada paso.

## Arquitectura

Estado hoisted simple (sin `androidx.lifecycle.ViewModel`): una clase
`HuellaAppState`, creada con `remember`, que guarda pestaña actual, estado del
drawer, modo emergencia (toggle de demo) y el estado del wizard de Reportar.
Datos simulados en un objeto `SampleData` con data classes tipadas
(`Mascota`, `Avistamiento`, `Refugio`, `Voluntario`, `Estadisticas`). Lógica
pura del wizard (avance de pasos, validación de campos requeridos) en
funciones top-level testeables con JUnit, ampliando `ReportFlowTest.kt`.

Se elige este enfoque (en vez de ViewModel + StateFlow por feature) porque no
hay backend ni llamadas asíncronas en esta entrega, y añadir esa capa sería
sobre-ingeniería para un MVP de presentación; además es el enfoque de menor
riesgo dado el código ya existente que se está refactorizando.

### Estructura de archivos

```
navigation/HuellaDestination.kt       enum bottom bar (Rescates, Mapa, Adopción, Comunidad, Perfil)
state/HuellaAppState.kt               estado app-level (pestaña, drawer, emergencia)
state/ReportFlowState.kt              estado del wizard (2 pasos) + funciones puras testeables
data/models.kt                       Mascota, Avistamiento, Refugio, Voluntario, Estadisticas, enums
data/SampleData.kt                   listas y valores simulados
ui/components/HuellaComponents.kt    chips de estado, action cards, timeline item, stat card
ui/home/RescatesScreen.kt            Home (emergencia/calma), acciones rápidas, casos cercanos
ui/report/ReportFlowScreen.kt        contenedor del wizard + navegación entre los 2 pasos
ui/report/ReportSteps.kt             paso 1 (tipo), paso 2 (formulario combinado), confirmación
ui/map/MapScreen.kt                  placeholder: lista simple de refugios/vets/voluntarios
ui/community/CommunityScreen.kt      placeholder mejorado
ui/adoption/AdoptionScreen.kt        placeholder simple (1-2 tarjetas de ejemplo)
ui/profile/ProfileScreen.kt          placeholder mejorado
ui/theme/{Color,Theme,Type}.kt       paleta de marca naranja, dynamicColor=false
HuellaApp.kt                        Scaffold: drawer + bottom bar + enrutado
MainActivity.kt                     solo entry point
```

## Testing

Se amplía `ReportFlowTest.kt` con la lógica pura del wizard de 2 pasos
(avance, validación de tipo requerido en paso 1, validación de campos
mínimos en paso 2). Pruebas de UI instrumentadas quedan fuera de esta
entrega.

## Pendiente explícito para fase 2

- Adopción funcional completa (RF32-35): publicar, consultar, manifestar
  interés, actualizar estado.
- Red de ayuda como sección dedicada (detalle de punto, solicitar ayuda,
  seguimiento de solicitud — RF23-31).
- Comunidad completa (feed, guías, historias, video).
- Perfil completo (mis mascotas, mi impacto, editar perfil).
- Mapa real con SDK de mapas, clustering, rutas.
- Autenticación y backend real (RF01-03), notificaciones push (RF36-39).
- Fuentes tipográficas de marca reales (Outfit/Public Sans vía Google Fonts).
- Cámara y GPS reales para foto/ubicación en Reportar.
- Evaluar si en algún punto se migra a la paleta verde/teal de los mockups de
  alta resolución (decisión de diseño pendiente, no técnica).
