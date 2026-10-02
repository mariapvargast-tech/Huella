package com.jmvr.rescatandohuellas.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.dp

/** Posición y alto del "pulgar" de la barra, en píxeles dentro del área visible. */
data class PulgarBarra(val inicio: Float, val alto: Float)

private const val ALTO_MINIMO_PULGAR_PX = 24f

/**
 * @param visible alto del área visible.
 * @param contenido alto total del contenido.
 * @param desplazado cuánto se ha desplazado el contenido desde arriba.
 * @return null si todo el contenido cabe y no hace falta barra.
 */
fun calcularPulgarBarra(visible: Float, contenido: Float, desplazado: Float): PulgarBarra? {
    if (visible <= 0f || contenido <= visible) return null
    val alto = (visible * visible / contenido).coerceIn(minOf(ALTO_MINIMO_PULGAR_PX, visible), visible)
    val progreso = (desplazado / (contenido - visible)).coerceIn(0f, 1f)
    return PulgarBarra(inicio = (visible - alto) * progreso, alto = alto)
}

/** Barra de desplazamiento visible para un contenedor con `verticalScroll(estado)`. Va antes de `verticalScroll`. */
fun Modifier.barraDesplazamiento(estado: ScrollState, color: Color): Modifier = drawWithContent {
    drawContent()
    val pulgar = calcularPulgarBarra(
        visible = size.height,
        contenido = size.height + estado.maxValue,
        desplazado = estado.value.toFloat()
    )
    dibujarBarra(pulgar, color)
}

/**
 * Barra de desplazamiento visible para un `LazyColumn`. Como la lista no conoce el alto de los
 * elementos que no ha dibujado, lo estima con el promedio de los visibles.
 */
fun Modifier.barraDesplazamiento(estado: LazyListState, color: Color): Modifier = drawWithContent {
    drawContent()
    val info = estado.layoutInfo
    val visibles = info.visibleItemsInfo
    if (visibles.isEmpty()) return@drawWithContent
    val altoPromedio = visibles.sumOf { it.size }.toFloat() / visibles.size
    val pulgar = calcularPulgarBarra(
        visible = size.height,
        contenido = altoPromedio * info.totalItemsCount + info.beforeContentPadding + info.afterContentPadding,
        desplazado = altoPromedio * estado.firstVisibleItemIndex + estado.firstVisibleItemScrollOffset
    )
    dibujarBarra(pulgar, color)
}

private fun ContentDrawScope.dibujarBarra(pulgar: PulgarBarra?, color: Color) {
    if (pulgar == null) return
    val ancho = 4.dp.toPx()
    val x = size.width - ancho - 2.dp.toPx()
    val radio = CornerRadius(ancho / 2)
    drawRoundRect(color.copy(alpha = 0.15f), Offset(x, 0f), Size(ancho, size.height), radio)
    drawRoundRect(color, Offset(x, pulgar.inicio), Size(ancho, pulgar.alto), radio)
}
