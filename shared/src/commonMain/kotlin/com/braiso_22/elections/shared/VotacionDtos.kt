package com.braiso_22.elections.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
enum class OpcionDto { CANDIDATURA, EN_BLANCO, CONTRA_EL_SISTEMA }

/** [candidaturaId] es obligatorio si [opcion] es CANDIDATURA y debe ir vacío en el resto. */
@JsExport
@Serializable
data class VotoRequest(
    val opcion: OpcionDto,
    val candidaturaId: String? = null,
)

@JsExport
@Serializable
data class VotoDto(
    val eleccionId: String,
    val opcion: OpcionDto,
    val candidaturaId: String?,
    /** Instante ISO-8601 de la última vez que se emitió o cambió. */
    val emitidoEn: String,
)
