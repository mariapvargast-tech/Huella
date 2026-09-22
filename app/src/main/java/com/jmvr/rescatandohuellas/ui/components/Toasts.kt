package com.jmvr.rescatandohuellas.ui.components

import android.content.Context
import android.widget.Toast

fun Context.mostrarProximaEntrega() {
    Toast.makeText(this, "Disponible en la próxima entrega", Toast.LENGTH_SHORT).show()
}
