@file:Suppress("ArrayInDataClass") // Array en vez de List para que TypeScript reciba arrays nativos

package com.braiso_22.elections.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
enum class EstadoEleccionDto { CONVOCADA, ABIERTA, CERRADA }

@JsExport
@Serializable
data class EleccionResumenDto(
    val id: String,
    val nombre: String,
    val estado: EstadoEleccionDto,
    /** Instante ISO-8601. */
    val inicioVotacion: String,
    /** Instante ISO-8601. */
    val finVotacion: String,
)

@JsExport
@Serializable
data class EleccionDto(
    val id: String,
    val referenciaBoe: String,
    val nombre: String,
    val estado: EstadoEleccionDto,
    val inicioVotacion: String,
    val finVotacion: String,
    val candidaturas: Array<CandidaturaDto>,
)

@JsExport
@Serializable
data class CandidaturaDto(
    val id: String,
    val partidoId: String,
    val denominacion: String,
)

@JsExport
@Serializable
data class ImportarEleccionRequest(
    val referenciaBoe: String,
    val inicioVotacion: String,
    val finVotacion: String,
)
