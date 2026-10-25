package com.braiso_22.elections.resultados.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.validar

/** Lo que cuenta Resultados de cada voto. Es su propio modelo: no depende del de Votación. */
sealed interface OpcionContada {
    data class Candidatura(val id: CandidaturaId) : OpcionContada
    data object EnBlanco : OpcionContada
    data object ContraElSistema : OpcionContada
}

/** Votos contados en una elección. Inmutable: cada voto produce un recuento nuevo. */
data class Recuento(
    val eleccionId: EleccionId,
    val porCandidatura: Map<CandidaturaId, Int> = emptyMap(),
    val enBlanco: Int = 0,
    val contraElSistema: Int = 0,
) {
    init {
        validar(enBlanco >= 0 && contraElSistema >= 0 && porCandidatura.values.all { it >= 0 }) {
            "Un recuento no puede tener votos negativos"
        }
    }

    /** Cada votante tiene un único voto, así que el total es también la participación. */
    val total: Int get() = porCandidatura.values.sum() + enBlanco + contraElSistema

    fun votosDe(opcion: OpcionContada): Int = when (opcion) {
        is OpcionContada.Candidatura -> porCandidatura[opcion.id] ?: 0
        OpcionContada.EnBlanco -> enBlanco
        OpcionContada.ContraElSistema -> contraElSistema
    }

    /** De 0 a 100 sobre el total de votos; 0 si aún no ha votado nadie. */
    fun porcentajeDe(opcion: OpcionContada): Double =
        if (total == 0) 0.0 else votosDe(opcion) * 100.0 / total

    fun sumar(opcion: OpcionContada): Recuento = ajustar(opcion, +1)

    fun cambiar(anterior: OpcionContada, nueva: OpcionContada): Recuento = ajustar(anterior, -1).ajustar(nueva, +1)

    private fun ajustar(opcion: OpcionContada, delta: Int): Recuento = when (opcion) {
        is OpcionContada.Candidatura -> copy(porCandidatura = porCandidatura + (opcion.id to votosDe(opcion) + delta))
        OpcionContada.EnBlanco -> copy(enBlanco = enBlanco + delta)
        OpcionContada.ContraElSistema -> copy(contraElSistema = contraElSistema + delta)
    }
}
