package com.braiso_22.elections.resultados.infrastructure

import com.braiso_22.elections.kernel.BusDeEventos
import com.braiso_22.elections.kernel.suscribir
import com.braiso_22.elections.resultados.application.ContabilizarVotos
import com.braiso_22.elections.resultados.domain.OpcionContada
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.VotoCambiado
import com.braiso_22.elections.votacion.domain.VotoEmitido

/** Capa anticorrupción: traduce los eventos de Votación al modelo de Resultados. */
class SuscriptorDeVotacion(
    private val bus: BusDeEventos,
    private val contabilizar: ContabilizarVotos,
) {
    fun suscribir() {
        bus.suscribir<VotoEmitido> { contabilizar.votoEmitido(it.eleccionId, it.opcion.toContada()) }
        bus.suscribir<VotoCambiado> {
            contabilizar.votoCambiado(it.eleccionId, it.anterior.toContada(), it.nueva.toContada())
        }
    }

    private fun Opcion.toContada(): OpcionContada = when (this) {
        is Opcion.ACandidatura -> OpcionContada.Candidatura(candidaturaId)
        Opcion.EnBlanco -> OpcionContada.EnBlanco
        Opcion.ContraElSistema -> OpcionContada.ContraElSistema
    }
}
