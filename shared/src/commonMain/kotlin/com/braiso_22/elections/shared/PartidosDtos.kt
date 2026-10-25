package com.braiso_22.elections.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class PartidoDto(
    val id: String,
    val nombre: String,
    val siglas: String,
)

@JsExport
@Serializable
data class ImportacionPartidosDto(
    val nuevos: Int,
    val actualizados: Int,
)
