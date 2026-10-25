package com.braiso_22.elections.votacion.application

import com.braiso_22.elections.kernel.BusDeEventos
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.VotanteId
import com.braiso_22.elections.votacion.domain.CandidaturaNoPresentada
import com.braiso_22.elections.votacion.domain.ConsultaElecciones
import com.braiso_22.elections.votacion.domain.EleccionDesconocida
import com.braiso_22.elections.votacion.domain.EleccionNoAbierta
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.RepositorioVotos
import com.braiso_22.elections.votacion.domain.Voto
import com.braiso_22.elections.votacion.domain.VotoNoEncontrado
import kotlin.time.Clock

/** Emite el voto o, si el votante ya había votado, lo cambia. */
class Votar(
    private val elecciones: ConsultaElecciones,
    private val votos: RepositorioVotos,
    private val eventos: BusDeEventos,
    private val reloj: Clock,
) {
    suspend operator fun invoke(eleccionId: EleccionId, votanteId: VotanteId, opcion: Opcion): Voto {
        val situacion = elecciones.situacion(eleccionId) ?: throw EleccionDesconocida(eleccionId)
        if (!situacion.admiteVotos) throw EleccionNoAbierta(eleccionId)
        if (opcion is Opcion.ACandidatura && opcion.candidaturaId !in situacion.candidaturas) {
            throw CandidaturaNoPresentada(eleccionId, opcion.candidaturaId)
        }

        val ahora = reloj.now()
        val voto = votos.de(eleccionId, votanteId)
            ?.apply { cambiar(opcion, ahora) }
            ?: Voto.emitir(eleccionId, votanteId, opcion, ahora)

        val pendientes = voto.extraerEventos()
        if (pendientes.isNotEmpty()) {
            votos.guardar(voto)
            eventos.publicar(pendientes)
        }
        return voto
    }
}

class ConsultarMiVoto(private val votos: RepositorioVotos) {
    suspend operator fun invoke(eleccionId: EleccionId, votanteId: VotanteId): Voto =
        votos.de(eleccionId, votanteId) ?: throw VotoNoEncontrado(eleccionId)
}
