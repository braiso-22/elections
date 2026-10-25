package com.braiso_22.elections.votacion.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.VotanteId

interface RepositorioVotos {
    suspend fun de(eleccionId: EleccionId, votanteId: VotanteId): Voto?

    /** @throws com.braiso_22.elections.kernel.ConflictoDeConcurrencia si otro proceso guardó el voto antes. */
    suspend fun guardar(voto: Voto)
}

/** Lo que Votación necesita saber de una elección, sin conocer el modelo del contexto Elecciones. */
interface ConsultaElecciones {
    suspend fun situacion(eleccionId: EleccionId): SituacionEleccion?
}

data class SituacionEleccion(
    val admiteVotos: Boolean,
    val candidaturas: Set<CandidaturaId>,
)
