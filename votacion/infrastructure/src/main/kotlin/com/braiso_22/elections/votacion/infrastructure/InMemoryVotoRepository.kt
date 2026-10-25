package com.braiso_22.elections.votacion.infrastructure

import com.braiso_22.elections.kernel.ConflictoDeConcurrencia
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.VotanteId
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.RepositorioVotos
import com.braiso_22.elections.votacion.domain.Voto
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Instant

/** Bloqueo optimista: solo se guarda si nadie ha guardado ese voto desde que se leyó. */
class InMemoryVotoRepository : RepositorioVotos {
    private val votos = ConcurrentHashMap<Clave, Fila>()

    override suspend fun de(eleccionId: EleccionId, votanteId: VotanteId): Voto? =
        votos[Clave(eleccionId, votanteId)]?.let {
            Voto.reconstruir(eleccionId, votanteId, it.opcion, it.emitidoEn, it.version)
        }

    override suspend fun guardar(voto: Voto) {
        votos.compute(Clave(voto.eleccionId, voto.votanteId)) { _, actual ->
            if ((actual?.version ?: 0) != voto.version) {
                throw ConflictoDeConcurrencia("El voto ha cambiado mientras se procesaba; vuelve a intentarlo")
            }
            Fila(voto.opcion, voto.emitidoEn, version = voto.version + 1)
        }
    }

    private data class Clave(val eleccionId: EleccionId, val votanteId: VotanteId)

    private data class Fila(val opcion: Opcion, val emitidoEn: Instant, val version: Int)
}
