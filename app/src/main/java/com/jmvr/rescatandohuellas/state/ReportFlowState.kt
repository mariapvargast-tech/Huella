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
