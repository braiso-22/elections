@file:Suppress("ArrayInDataClass") // Array en vez de List para que TypeScript reciba arrays nativos

package com.braiso_22.elections.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ResultadosDto(
    val eleccionId: String,
    /** Número de votantes que han votado. */
    val participacion: Int,
    val enBlanco: CifraDto,
    val contraElSistema: CifraDto,
    /** Ordenadas de más a menos votos. */
    val candidaturas: Array<ResultadoCandidaturaDto>,
)

@JsExport
@Serializable
data class ResultadoCandidaturaDto(
    val candidaturaId: String,
    val denominacion: String,
    val cifra: CifraDto,
)

/** [porcentaje] va de 0 a 100 sobre el total de votos emitidos. */
@JsExport
@Serializable
data class CifraDto(
    val votos: Int,
    val porcentaje: Double,
)
