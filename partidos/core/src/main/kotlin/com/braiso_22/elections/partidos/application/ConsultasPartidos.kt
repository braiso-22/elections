package com.braiso_22.elections.partidos.application

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.partidos.domain.PartidoNoEncontrado
import com.braiso_22.elections.partidos.domain.RepositorioPartidos

class ListarPartidos(private val partidos: RepositorioPartidos) {
    suspend operator fun invoke(busqueda: String? = null): List<Partido> {
        val texto = busqueda?.trim().orEmpty()
        return partidos.todos()
            .filter { texto.isEmpty() || it.coincideCon(texto) }
            .sortedBy { it.nombre }
    }
}

class ObtenerPartido(private val partidos: RepositorioPartidos) {
    suspend operator fun invoke(id: PartidoId): Partido =
        partidos.porId(id) ?: throw PartidoNoEncontrado(id)
}

/** Lo usa Elecciones para casar las candidaturas del BOE, que vienen por siglas, con el catálogo. */
class BuscarPartidoPorSiglas(private val partidos: RepositorioPartidos) {
    suspend operator fun invoke(siglas: String): Partido? =
        partidos.todos().firstOrNull { it.siglas.equivalenA(siglas) }
}
