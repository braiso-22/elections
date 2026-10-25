package com.braiso_22.elections.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ErrorDto(
    /** Nombre del error de dominio, p. ej. "EleccionNoAbierta". */
    val codigo: String,
    val mensaje: String,
)
