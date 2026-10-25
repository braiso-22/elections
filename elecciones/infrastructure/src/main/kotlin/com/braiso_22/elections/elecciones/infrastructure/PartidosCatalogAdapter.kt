package com.braiso_22.elections.elecciones.infrastructure

import com.braiso_22.elections.elecciones.domain.CatalogoDePartidos
import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.application.BuscarPartidoPorSiglas

/** Traduce la pregunta de Elecciones a un caso de uso del contexto Partidos. */
class PartidosCatalogAdapter(private val buscarPartidoPorSiglas: BuscarPartidoPorSiglas) : CatalogoDePartidos {
    override suspend fun partidoConSiglas(siglas: String): PartidoId? = buscarPartidoPorSiglas(siglas)?.id
}
