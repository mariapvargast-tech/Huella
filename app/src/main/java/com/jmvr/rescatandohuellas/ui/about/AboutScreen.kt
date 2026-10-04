package com.jmvr.rescatandohuellas.ui.about

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jmvr.rescatandohuellas.state.URL_LANDING_OFFLINE
import com.jmvr.rescatandohuellas.state.URL_LANDING_POR_DEFECTO
import com.jmvr.rescatandohuellas.state.normalizarUrl

@Composable
fun AboutScreen(
    urlCargada: String,
    onCargarUrl: (String) -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onCerrar),
                contentAlignment = Alignment.Center
            ) { Text("‹") }
            Spacer(modifier = Modifier.size(12.dp))
            Text("Acerca de", style = MaterialTheme.typography.titleLarge)
        }

        BarraUrl(urlCargada = urlCargada, onCargarUrl = onCargarUrl)

        LandingWebView(url = urlCargada, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun BarraUrl(urlCargada: String, onCargarUrl: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    // Se reinicia con cada URL cargada para que el campo muestre siempre la dirección actual.
    var texto by rememberSaveable(urlCargada) { mutableStateOf(urlCargada) }
    var invalida by rememberSaveable(urlCargada) { mutableStateOf(false) }

    fun cargar(nueva: String) {
        val url = normalizarUrl(nueva)
        if (url == null) {
            invalida = true
            return
        }
        focusManager.clearFocus()
        texto = url
        onCargarUrl(url)
    }

    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BasicTextField + DecorationBox en lugar de OutlinedTextField, que exige 56 dp de alto mínimo.
            BasicTextField(
                value = texto,
                onValueChange = {
                    texto = it
                    invalida = false
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { cargar(texto) }),
                interactionSource = interactionSource,
                modifier = Modifier
                    .weight(1f)
                    .height(ALTO_BARRA_URL)
            ) { campo ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = texto,
                    innerTextField = campo,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = invalida,
                    placeholder = { Text("Escribe una URL", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = if (texto != URL_LANDING_POR_DEFECTO) {
                        {
                            IconButton(
                                onClick = { cargar(URL_LANDING_POR_DEFECTO) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Restore,
                                    contentDescription = "Restablecer URL por defecto",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else null,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = invalida,
                            interactionSource = interactionSource
                        )
                    }
                )
            }
            Button(
                onClick = { cargar(texto) },
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(ALTO_BARRA_URL)
            ) {
                Text("Ir")
            }
        }
        if (invalida) {
            Text(
                "Escribe una URL válida, por ejemplo google.com",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

private val ALTO_BARRA_URL = 40.dp

@Composable
private fun LandingWebView(url: String, modifier: Modifier = Modifier) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val urlActual by rememberUpdatedState(url)

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError
                    ) {
                        // Sin conexión, la URL por defecto cae a la copia local del landing.
                        // Con una URL propia se deja la página de error del WebView para ver qué falló.
                        if (request.isForMainFrame && urlActual == URL_LANDING_POR_DEFECTO) {
                            view.loadUrl(URL_LANDING_OFFLINE)
                        }
                    }
                }
            }.also { webView = it }
        }
    )

    LaunchedEffect(webView, url) {
        webView?.loadUrl(url)
    }
}
