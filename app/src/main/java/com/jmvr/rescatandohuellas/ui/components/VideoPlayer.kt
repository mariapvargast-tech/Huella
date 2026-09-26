package com.jmvr.rescatandohuellas.ui.components

import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(@RawRes videoRes: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val player = remember(videoRes) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$videoRes"))
            prepare()
        }
    }
    var pantallaCompleta by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycle, player) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) player.pause()
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            player.release()
        }
    }

    LaunchedEffect(pantallaCompleta) {
        if (!player.isPlaying) player.seekTo(player.currentPosition)
    }

    VistaReproductor(
        player = if (pantallaCompleta) null else player,
        pantallaCompleta = false,
        onCambiarPantallaCompleta = { pantallaCompleta = it },
        modifier = modifier
    )

    if (pantallaCompleta) {
        Dialog(
            onDismissRequest = { pantallaCompleta = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            OcultarBarrasDelSistema()
            VistaReproductor(
                player = player,
                pantallaCompleta = true,
                onCambiarPantallaCompleta = { pantallaCompleta = it },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VistaReproductor(
    player: Player?,
    pantallaCompleta: Boolean,
    onCambiarPantallaCompleta: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                setFullscreenButtonClickListener { onCambiarPantallaCompleta(it) }
            }
        },
        update = { vista ->
            vista.player = player
            vista.setFullscreenButtonState(pantallaCompleta)
        },
        modifier = modifier
    )
}

@Composable
private fun OcultarBarrasDelSistema() {
    val vista = LocalView.current
    DisposableEffect(vista) {
        val ventana = (vista.parent as? DialogWindowProvider)?.window
        val controlador = ventana?.let { WindowCompat.getInsetsController(it, vista) }
        controlador?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controlador?.hide(WindowInsetsCompat.Type.systemBars())
        onDispose { controlador?.show(WindowInsetsCompat.Type.systemBars()) }
    }
}
