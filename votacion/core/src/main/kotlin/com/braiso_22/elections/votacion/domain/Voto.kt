package com.braiso_22.elections.votacion.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.EventoDeDominio
import com.braiso_22.elections.kernel.RaizDeAgregado
import com.braiso_22.elections.kernel.VotanteId
import kotlin.time.Instant

/**
 * El voto de un votante en una elección. Hay uno como mucho por (elección, votante) y no es
 * secreto: queda asociado a quien lo emite para que pueda cambiarlo mientras la votación siga abierta.
 */
class Voto private constructor(
    val eleccionId: EleccionId,
    val votanteId: VotanteId,
    opcion: Opcion,
    emitidoEn: Instant,
    /** Versión con la que se leyó; el repositorio la usa para detectar escrituras concurrentes. */
    val version: Int,
) : RaizDeAgregado() {
    var opcion: Opcion = opcion
        private set
    var emitidoEn: Instant = emitidoEn
        private set

    /** Votar lo mismo otra vez no cambia nada ni genera eventos. */
    fun cambiar(nueva: Opcion, ahora: Instant) {
        if (nueva == opcion) return
        val anterior = opcion
        opcion = nueva
        emitidoEn = ahora
        registrar(VotoCambiado(eleccionId, votanteId, anterior, nueva))
    }

    private fun registrarEmision() = registrar(VotoEmitido(eleccionId, votanteId, opcion))

    companion object {
        fun emitir(eleccionId: EleccionId, votanteId: VotanteId, opcion: Opcion, ahora: Instant): Voto =
            Voto(eleccionId, votanteId, opcion, ahora, version = 0).apply { registrarEmision() }

        /** Para los repositorios: reconstruye un voto ya guardado. */
        fun reconstruir(eleccionId: EleccionId, votanteId: VotanteId, opcion: Opcion, emitidoEn: Instant, version: Int) =
            Voto(eleccionId, votanteId, opcion, emitidoEn, version)
    }
}

sealed interface Opcion {
    data class ACandidatura(val candidaturaId: CandidaturaId) : Opcion

    /** Delega el voto en lo que decida la mayoría. */
    data object EnBlanco : Opcion

    /** Rechazo a todas las candidaturas. No existe en el sistema electoral real. */
    data object ContraElSistema : Opcion
}

data class VotoEmitido(
    val eleccionId: EleccionId,
    val votanteId: VotanteId,
    val opcion: Opcion,
) : EventoDeDominio

data class VotoCambiado(
    val eleccionId: EleccionId,
    val votanteId: VotanteId,
    val anterior: Opcion,
    val nueva: Opcion,
) : EventoDeDominio
