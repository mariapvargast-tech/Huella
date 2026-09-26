package com.jmvr.rescatandohuellas.ui.map

import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jmvr.rescatandohuellas.data.ReporteMapa
import com.jmvr.rescatandohuellas.data.TipoReporte
import com.jmvr.rescatandohuellas.ui.theme.HuellaCoral
import com.jmvr.rescatandohuellas.ui.theme.HuellaGreen
import com.jmvr.rescatandohuellas.ui.theme.HuellaOrange
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
private const val ESTILO_MAPA = "https://tiles.openfreemap.org/styles/liberty"
private const val FUENTE_REPORTES = "reportes"
private const val CAPA_REPORTES = "reportes-pines"
private const val MARGEN_TOQUE_PX = 24f

private val CENTRO_INICIAL = LatLng(6.2442, -75.5900)
private const val ZOOM_INICIAL = 12.9

fun colorDeReporte(tipo: TipoReporte) = when (tipo) {
    TipoReporte.PERDIDA -> HuellaOrange
    TipoReporte.ENCONTRADA -> HuellaGreen
    TipoReporte.RIESGO -> HuellaCoral
}

@Composable
fun ReportesMapa(
    reportes: List<ReporteMapa>,
    seleccionadoId: Int?,
    onSeleccionar: (Int?) -> Unit,
    onMantenerPresionado: (latitud: Double, longitud: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply { onCreate(null) }
    }
    var estilo by remember { mutableStateOf<Style?>(null) }
    val onSeleccionarActual by rememberUpdatedState(onSeleccionar)
    val onMantenerPresionadoActual by rememberUpdatedState(onMantenerPresionado)

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onPause()
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStop()
            mapView.onDestroy()
        }
    }

    DisposableEffect(mapView) {
        mapView.getMapAsync { mapa ->
            mapa.cameraPosition = CameraPosition.Builder()
                .target(CENTRO_INICIAL)
                .zoom(ZOOM_INICIAL)
                .build()
            mapa.setStyle(Style.Builder().fromUri(ESTILO_MAPA)) { style ->
                style.addSource(GeoJsonSource(FUENTE_REPORTES))
                style.addLayer(
                    CircleLayer(CAPA_REPORTES, FUENTE_REPORTES).withProperties(
                        PropertyFactory.circleColor(
                            Expression.match(
                                Expression.get("tipo"),
                                Expression.color(HuellaOrange.toArgb()),
                                *TipoReporte.entries.map { tipo ->
                                    Expression.stop(tipo.name, Expression.color(colorDeReporte(tipo).toArgb()))
                                }.toTypedArray()
                            )
                        ),
                        PropertyFactory.circleStrokeColor(android.graphics.Color.WHITE),
                        PropertyFactory.circleStrokeWidth(2.5f)
                    )
                )
                estilo = style
            }
            mapa.addOnMapClickListener { punto ->
                val pantalla = mapa.projection.toScreenLocation(punto)
                val area = RectF(
                    pantalla.x - MARGEN_TOQUE_PX, pantalla.y - MARGEN_TOQUE_PX,
                    pantalla.x + MARGEN_TOQUE_PX, pantalla.y + MARGEN_TOQUE_PX
                )
                val id = mapa.queryRenderedFeatures(area, CAPA_REPORTES)
                    .firstOrNull()
                    ?.getNumberProperty("id")
                    ?.toInt()
                onSeleccionarActual(id)
                true
            }
            mapa.addOnMapLongClickListener { punto ->
                onMantenerPresionadoActual(punto.latitude, punto.longitude)
                true
            }
        }
        onDispose { estilo = null }
    }

    LaunchedEffect(estilo, reportes, seleccionadoId) {
        val style = estilo ?: return@LaunchedEffect
        val pines = reportes.map { reporte ->
            Feature.fromGeometry(Point.fromLngLat(reporte.longitud, reporte.latitud)).apply {
                addNumberProperty("id", reporte.id)
                addStringProperty("tipo", reporte.tipo.name)
            }
        }
        style.getSourceAs<GeoJsonSource>(FUENTE_REPORTES)?.setGeoJson(FeatureCollection.fromFeatures(pines))
        style.getLayer(CAPA_REPORTES)?.setProperties(
            PropertyFactory.circleRadius(
                Expression.switchCase(
                    Expression.eq(Expression.get("id"), Expression.literal(seleccionadoId ?: -1)),
                    Expression.literal(12f),
                    Expression.literal(8f)
                )
            )
        )
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}
