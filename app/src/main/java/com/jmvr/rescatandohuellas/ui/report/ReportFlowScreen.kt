package com.jmvr.rescatandohuellas.ui.report

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jmvr.rescatandohuellas.state.PASO_CONFIRMACION
import com.jmvr.rescatandohuellas.state.PASO_DATOS
import com.jmvr.rescatandohuellas.state.PASO_TIPO
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeDatos
import com.jmvr.rescatandohuellas.state.puedeAvanzarDesdeTipo
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
            if (manager.paso != PASO_CONFIRMACION) {
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
            }
            Text(
                "Reportar caso",
                style = MaterialTheme.typography.titleLarge
            )
        }

            Navegador(
                url = "https://www.google.com",
                modifier = Modifier
                    .fillMaxSize()
            )

    }
}

@Composable
fun Navegador(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        modifier = modifier
    )
}